package parser;

import employee.Employee;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

public class EmployeeParser {
    public static ArrayList<Employee> parse(String filePath, String sheetName) throws IOException, NumberFormatException {
        ArrayList<Employee> employees = new ArrayList<>();

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
            Cell nameCell = row.getCell(1);
            Cell activeTimeCell = row.getCell(2);
            Cell inactiveTimeCell = row.getCell(3);
            Cell timeAtWorkCell = row.getCell(4);

            int id = (int) idCell.getNumericCellValue();
            String name = nameCell.getStringCellValue();
            double totalActiveTime = Math.round(activeTimeCell.getNumericCellValue() * 100.0) / 100.0;
            double totalInactiveTime = Math.round(inactiveTimeCell.getNumericCellValue() * 100.0) / 100.0;
            double timeAtWork = Math.round(timeAtWorkCell.getNumericCellValue() * 100.0) / 100.0;

            Employee employee = new Employee(id, name, totalActiveTime, totalInactiveTime, timeAtWork);
            employees.add(employee);
        }
        workbook.close();
        fis.close();

        return employees;
    }

    public static void serialize(String filePath, String sheetName, ArrayList<Employee> employees) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet(sheetName);

        for (int i = sheet.getLastRowNum(); i >= 1; i--) {
            Row row = sheet.getRow(i);
            if (row != null) {
                sheet.removeRow(row);
            }
        }

        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(employee.getEmployeeId());
            row.createCell(1).setCellValue(employee.getEmployeeName());
            row.createCell(2).setCellValue(employee.getTotalActiveTime());
            row.createCell(3).setCellValue(employee.getTotalInactiveTime());
            row.createCell(4).setCellValue(employee.getTimeAtWork());
        }

        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);
        workbook.close();
    }
}