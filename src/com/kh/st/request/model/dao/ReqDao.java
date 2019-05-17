package com.kh.st.request.model.dao;

import static com.kh.st.common.JDBCTemplate.close;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;

import com.kh.st.common.PageInfo;
import com.kh.st.request.model.vo.ReqProduct;

public class ReqDao {
	private Properties prop = new Properties();
	
	public ReqDao() {
		String fileName = ReqDao
							.class
							.getResource("/sql/request/request-query.properties")
							.getPath();
		try {
			prop.load(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//페이징 처리 전체 조회 게시글 숫자
	public int getListCount(Connection con) {
		Statement stmt = null;
		ResultSet rset = null;
		int listCount = 0;
		
		String query = prop.getProperty("listCount");
		try {
			stmt = con.createStatement();
			rset = stmt.executeQuery(query);
			
			if(rset.next()) {
				listCount = rset.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(stmt);
			close(rset);
		}
		return listCount;
	}
	//페이징 처리 전체 조회
	public ArrayList<ReqProduct> reqList(Connection con, PageInfo pi) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ArrayList<ReqProduct> list = null;
		
		String qeury = prop.getProperty("reqList");
		
		int startRow = (pi.getCurrentPage() - 1) * pi.getLimit() + 1;
		int endRow = startRow + pi.getLimit() - 1;
		
		try {
			pstmt = con.prepareStatement(qeury);
			pstmt.setInt(1, startRow);
			pstmt.setInt(2, endRow);
			
			rset = pstmt.executeQuery();
			
			list = new ArrayList<ReqProduct>();
			
			while(rset.next()) {
				ReqProduct rp = new ReqProduct();
				
				rp.setUpNo(rset.getInt("REQP_NO"));
				rp.setbWriter(rset.getString("USER_NAME"));
				rp.setProductName(rset.getString("CTG_NAME"));
				rp.setReqDate(rset.getDate("REQP_DATE"));
				rp.setbTitle(rset.getString("BTITLE"));
				rp.setStatus(rset.getString("STATUS"));
				
				list.add(rp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return list;
	}
	
	//선택한 요청 거절 조회
	public ArrayList<ReqProduct> reqNoSelect(Connection con, String[] status) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ArrayList<ReqProduct> list = new ArrayList<ReqProduct>();
		
		String query = prop.getProperty("reqNoSelect");
		try {
			for(int i = 0; i < status.length; i++) {
				pstmt = con.prepareStatement(query);
				pstmt.setInt(1, Integer.parseInt(status[i]));
				
				rset = pstmt.executeQuery();
				
				while(rset.next()) {
					ReqProduct rp = new ReqProduct();
					
					rp.setUpNo(rset.getInt("REQP_NO"));
					rp.setProductName(rset.getString("CTG_NAME"));
					rp.setbWriter(rset.getString("USER_NAME"));
					
					list.add(rp);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(rset);
		}
		System.out.println(list);
		return list;
	}
	
	//요청 거절 처리
	public int reqNo(Connection con, String[] num) {
		PreparedStatement pstmt = null;
		int result = 0;
		
		String query = prop.getProperty("reqNo");
		
		try {
			for(int i = 0; i < num.length; i++) {
				pstmt = con.prepareStatement(query);
				pstmt.setInt(1, Integer.parseInt(num[i]));
				
				result += pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return result;
	}
	
	//요청 거절 결과(사유 입력)
	public int reqNoResult(Connection con, String[] num, String textResult) {
		PreparedStatement pstmt = null;
		int result1 = 0;
		
		String query = prop.getProperty("reqNoResult");
		try {
			for(int i = 0; i < num.length; i++) {
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, textResult);
				pstmt.setInt(2, Integer.parseInt(num[i]));
				
				result1 += pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return result1;
	}
	
	//선택한 요청 승인 조회
	public ReqProduct reqOkSelect(Connection con, String status) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ReqProduct rp = null;
		
		String query = prop.getProperty("reqOkSelect");
		
		try {
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(status));
			
			rset = pstmt.executeQuery();
			
			if(rset.next()) {
				rp = new ReqProduct();
				
				rp.setUpNo(rset.getInt("REQP_NO"));
				rp.setProductName(rset.getString("CTG_NAME"));
				rp.setbWriter(rset.getString("USER_NAME"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
			close(rset);
		}
		return rp;
	}
	
	//요청 승인 처리
	public int reqOk(Connection con, int num) {
		PreparedStatement pstmt = null;
		int result = 0;
		
		String query = prop.getProperty("reqOk");
		
		try {
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, num);
			
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return result;
	}
	
	//요청 승인 처리(승인 날짜)
	public int reqOkResult(Connection con, int num) {
		PreparedStatement pstmt = null;
		int result1 = 0;
		
		String query = prop.getProperty("reqOkResult");
		
		try {
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, num);
			
			result1 = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return result1;
	}
	
	//요청 승인 배송 처리
	public int delWait(Connection con, int num, String delivery, int dNo) {
		PreparedStatement pstmt = null;
		int result2 = 0;
		String reg = "등록";
		String in = "입고";
		
		String query = prop.getProperty("delWait");
		try {
			pstmt = con.prepareStatement(query);
			pstmt.setInt(1, dNo);
			pstmt.setString(2, delivery);
			pstmt.setString(3, reg);
			pstmt.setInt(4, num);
			pstmt.setString(5, in);
			pstmt.setInt(6, num);
			
			result2 = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(pstmt);
		}
		return result2;
	}
}
