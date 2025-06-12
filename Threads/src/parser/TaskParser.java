package parser;

import task.Status;
import task.Task;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class TaskParser {
    public static ArrayList<Task> parse(String filePath, String sheetName) throws IOException, NumberFormatException {
        ArrayList<Task> tasks = new ArrayList<>();

        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        Iterator<Row> rowIterator = sheet.iterator();
        boolean isStart = true;
        while (rowIterator.hasNext()) {
            if (isStart) {
                rowIterator.next();
                isStart = false;
                continue;
            }

            Row row = rowIterator.next();
            Cell idCell = row.getCell(0);
            Cell employeeIdCell = row.getCell(1);
            Cell descriptionCell = row.getCell(2);
            Cell requiredTimeCell = row.getCell(3);
            Cell spentTimeCell = row.getCell(4);
            Cell statusCell = row.getCell(5);

            int id = (int) idCell.getNumericCellValue();
            int employeeId = (int) employeeIdCell.getNumericCellValue();
            String description = descriptionCell.getStringCellValue();
            double requiredTime = Math.round(requiredTimeCell.getNumericCellValue() * 100.0) / 100.0;
            double spentTime = Math.round(spentTimeCell.getNumericCellValue() * 100.0) / 100.0;
            Status status;
            switch (statusCell.getStringCellValue()) {
                case "IN PROCESS":
                    status = Status.IN_PROCESS;
                    break;
                case "DONE":
                    status = Status.DONE;
                    break;
                default:
                    status = Status.NEW;
                    break;
            }

            Task task = new Task(id, employeeId, description, requiredTime, spentTime, status);
            tasks.add(task);
        }
        workbook.close();
        fis.close();

        return tasks;
    }

    public static void serialize(String filePath, String sheetName, ArrayList<Task> tasks) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        for (int i = sheet.getLastRowNum(); i >= 1; i--) {
            Row row = sheet.getRow(i);
            if (row != null) {
                sheet.removeRow(row);
            }
        }

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(task.getId());
            row.createCell(1).setCellValue(task.getEmployeeId());
            row.createCell(2).setCellValue(task.getDescription());
            row.createCell(3).setCellValue(task.getRequiredTime());
            row.createCell(4).setCellValue(task.getSpentTime());
            String status;
            switch (task.getStatus()) {
                case Status.DONE:
                    status = "DONE";
                    break;
                case Status.IN_PROCESS:
                    status = "IN PROCESS";
                    break;
                default:
                    status = "NEW";
                    break;
            }
            row.createCell(5).setCellValue(status);
        }

        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);
        workbook.close();
    }
}