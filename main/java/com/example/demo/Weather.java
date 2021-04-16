package com.example.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Weather {

	static int rowNo = 0;
	
	public static void main(String[] args) throws Exception {

		Weather t = new Weather();
		String name = "";
		String strMonth = "";
		String strDay = "";
		String strTime = "";
		String strMin = "";

		for (int year = 2018; year <= 2021; year++)
		{
			HSSFWorkbook workBook = new HSSFWorkbook();
			name = "" + year;
			for(int month = 01; month <= 12; month++)
			{
				HSSFSheet sheet = workBook.createSheet(""+month);
				rowNo = 0;
				if(month < 10)
				{
					//날짜 일시 시간 수정이 필요함
					strMonth = "0" + month;
				}else {
					strMonth = "" + month;
				}
				for(int day = 01; day <= 31; day++)
				{
					if(day < 10)
					{
						//날짜 일시 시간 수정이 필요함
						strDay = "0" + day;
					}else {
						strDay = "" + day;
					}
					for(int time = 00; time <= 23; time+= 6)
					{
						if(time < 10)
						{
							//날짜 일시 시간 수정이 필요함
							strTime = "0" + time;
						}else {
							strTime = "" + time;
						}
						
						String weatherURL = "http://www.weather.go.kr/cgi-bin/aws/nph-aws_txt_min_guide_test?"+ year + strMonth + strDay + strTime + "00&0&MINDB_10M&172&m&K";
						
						try
						{
							Document tempDoc = Jsoup.connect(weatherURL).ignoreContentType(true).get();
							if(tempDoc != null)
							{
								Elements temperature = tempDoc.getElementsByClass("text");
								System.out.println(year +"-"+ strMonth +"-"+ strDay +"-"+ strTime+ strMin);
								t.inputData(sheet, temperature, 0, 2);
							}
						} catch (Exception e)
						{
							System.out.println(year +"-"+ strMonth +"-"+ strDay +"-"+ strTime + strMin + " : " + e);
						}
					}
				}
			}
			t.download(workBook, name);
			workBook.close();
		}
	}

	public void inputData(HSSFSheet sheet, Elements temperature, int startNo, int endNo) throws ParseException {
		
		int num = startNo;
		int cnt = 0;
		for(Element temp : temperature)
		{
			if(cnt == 37)
			{
				break;
			}
			String child = temp.childNode(0).toString();
			
			HSSFRow row = sheet.createRow(rowNo++);
			HSSFCell cell = row.createCell(startNo);
			
			//날짜
			cell.setCellValue(child.substring((child.indexOf("select(") + 12), child.lastIndexOf(");")));
			cell = row.createCell(++startNo);
			//온도
			cell.setCellValue(temp.child(8).text());
			cell = row.createCell(++startNo);
			//습도
			cell.setCellValue(temp.child(15).text());
			
			if(startNo == endNo)
			{
				startNo = num;
			}
			cnt++;
		}
	}

	public String timeStamp(Object ts) {
		Date date;
		Long time = Long.parseLong(ts + "");

		date = new Date(time * 1000L);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));

		String formattedDate = sdf.format(date);

		return formattedDate;
	}

	public void download(HSSFWorkbook workBook, String name) throws Exception {
		System.out.println("download");
		String path = "D:/down/";
		String fileName = name + ".xls";
		File file = new File(path + fileName);
		FileOutputStream fos = new FileOutputStream(file);

		workBook.write(fos);
	}
}
