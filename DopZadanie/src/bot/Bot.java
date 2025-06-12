package bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    public final List<Thread> activeThreads = new ArrayList<>();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();

            if (text.equalsIgnoreCase("/start")) {
                sendMsg(chatId, "Привет, введи /download и перечисли url");
            }

            if (text.startsWith("/download")) {
                String[] urls = text.split(" ");
                if (urls.length < 2) {
                    sendMsg(chatId, "Укажите URL файлов через пробел!");
                    return;
                }

                sendMsg(chatId, "Начинаю загрузку " + (urls.length - 1) + " файлов...");

                for (int i = 1; i < urls.length; i++) {
                    String url = urls[i];
                    Thread downloadThread = new Thread(() -> {
                        try {
                            String fileName = saveFileFromUrl(url);
                            sendMsg(chatId, "Успешно загружен файл: " + fileName);
                        } catch (IOException e) {
                            sendMsg(chatId, "Ошибка: " + url + " (" + e.getMessage() + ")");
                        }
                    });
                    downloadThread.start();
                    activeThreads.add(downloadThread);
                }
            }
        }
    }

    private String saveFileFromUrl(String fileUrl) throws IOException {
        System.out.println("Скачивание файла по URL: " + fileUrl);
        System.out.println("Текущая рабочая директория: " + System.getProperty("user.dir"));

        String fileName = fileUrl.substring(fileUrl.lastIndexOf('/') + 1);

        Path targetDir = Paths.get("src", "downloaded");
        System.out.println("Попытка сохранения в: " + targetDir);

        Path targetPath = targetDir.resolve(fileName);

        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("HEAD");
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Сервер вернул код: " + responseCode);
        }

        try (InputStream in = url.openStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Файл успешно сохранён: " + targetPath);
            return targetPath.toString();
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении файла:" + e.getMessage());
            throw e;
        }
    }

    private void sendMsg(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "downloaderUrlBot_bot";
    }

    @Override
    public String getBotToken() {
        return "8079306336:AAEiHzFGXqtblqS0ckoaMo6uiyFaSOz3UWc";
    }

    public void start() {
        System.out.println("Бот запущен.");
    }
}