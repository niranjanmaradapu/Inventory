package com.otsi.retail.inventory.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelService<T> {

	public List<T> readExcel(InputStream fis, Class<T> cls)
			throws IOException, InstantiationException, IllegalAccessException {

		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sheet = wb.getSheetAt(0);
		List<String> headerList = new ArrayList<>();
		List<T> productTextileVOList = new ArrayList<>();

		for (Row row : sheet) {
			T testExcel = cls.newInstance();
			for (Cell cell : row) {
				if (row.getRowNum() == 0) {
					headerList.add(cell.getStringCellValue());
				} else {
					String headerName = headerList.get(cell.getColumnIndex());
					switch (cell.getCellType()) {
					case NUMERIC:
						handleNumericType(testExcel, headerName, cell);
						break;
					case STRING:
						String stringCellValue = cell.getStringCellValue();
						callSetter(testExcel, headerName, stringCellValue);
						break;
					}
				}
			}
			if (row.getRowNum() > 0)
				productTextileVOList.add(testExcel);
		}
		System.out.println("list details " + productTextileVOList.toString());
		wb.close();
		return productTextileVOList;

	}

	public void callSetter(Object obj, String fieldName, Object value) {
		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor(fieldName, obj.getClass());
			Method method = pd.getWriteMethod();
			if (method != null) {
				method.invoke(obj, value);
			}
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

		}
	}

	public Object callGetter(Object obj, String fieldName) {
		PropertyDescriptor pd;
		try {
			pd = new PropertyDescriptor(fieldName, obj.getClass());
			Method method = pd.getReadMethod();
			if (method != null) {
				return method.invoke(obj);
			}
		} catch (IntrospectionException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
		}
		return null;
	}

	public Object handleNumericType(T testExcel, String fieldName, Cell cell) {
		Field[] fields = testExcel.getClass().getDeclaredFields();
		for (Field f : fields) {
			if (f.getName().equals(fieldName)) {

				if (f.getType().getName().equals("java.lang.int") || f.getType().getName().equals("int")) {
					int doubleCellValue = (int) cell.getNumericCellValue();
					callSetter(testExcel, fieldName, doubleCellValue);
				}

				else if (f.getType().getName().equals("java.lang.float") || f.getType().getName().equals("float")) {
					float doubleCellValue = (float) cell.getNumericCellValue();
					callSetter(testExcel, fieldName, doubleCellValue);
				}

				else if (f.getType().getName().equals("java.lang.Long") || f.getType().getName().equals("long")) {
					long doubleCellValue = (long) cell.getNumericCellValue();
					callSetter(testExcel, fieldName, doubleCellValue);
				}

				else if (f.getType().getName().equals("java.lang.String") || f.getType().getName().equals("String")) {
					String doubleCellValue = String.valueOf(cell.getNumericCellValue());
					callSetter(testExcel, fieldName, doubleCellValue);
				} else {
					double doubleCellValue = cell.getNumericCellValue();
					callSetter(testExcel, fieldName, doubleCellValue);
				}
			}

		}
		return testExcel;
	}

}