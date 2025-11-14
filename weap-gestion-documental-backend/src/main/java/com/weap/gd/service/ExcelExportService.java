package com.weap.gd.service;

import java.io.IOException;
import java.util.List;

public interface ExcelExportService<T> {
	
	byte[] exportToExcel(List<T> data) throws IOException;

}
