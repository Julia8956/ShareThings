package com.kh.st.board.model.service;

import static com.kh.st.common.JDBCTemplate.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.kh.st.attachment.model.vo.Attachment;
import com.kh.st.board.model.dao.BoardDao;
import com.kh.st.board.model.vo.Board;
import com.kh.st.product.model.dao.ProductDao;
import com.kh.st.product.model.vo.PCategory;

public class BoardService {

	//카테고리 메뉴 선택시 상품 리스트 불러오기
	public HashMap<String, Object> selectList(String ctgLv2) {
		Connection con = getConnection();
		ArrayList<PCategory> ctgList = new ProductDao().selectCtgLv3List(con, ctgLv2);
		ArrayList<HashMap<String, Object>> bList = new BoardDao().selectList(con, ctgLv2);
		HashMap<String, Object> listMap = new HashMap<String, Object>();
		listMap.put("ctgList", ctgList);
		listMap.put("bList", bList);
		close(con);
		return listMap;
	}

	//상품 게시판에서 필터 적용시
	public HashMap<String, Object> selectFilterList(HashMap<String, Object> filterMap) {
		Connection con = getConnection();
		ArrayList<PCategory> ctgList = new ProductDao().selectCtgLv3List(con, filterMap.get("ctgLv2").toString());
		ArrayList<HashMap<String, Object>> bList = new BoardDao().selectFilterList(con, filterMap);
		HashMap<String, Object> listMap = new HashMap<String, Object>();
		listMap.put("ctgList", ctgList);
		listMap.put("bList", bList);
		close(con);
		return listMap;
	}

	public HashMap<String, Object> selectOneBoard(int bno) {
		Connection con = getConnection();
		HashMap<String, Object> bDetailMap = new HashMap<String, Object>(); 
		
		int result = new BoardDao().updatebCount(con, bno);
		HashMap<String, Object> bmap = null;
		if (result > 0) {
			commit(con);
			bmap = new BoardDao().selectOneBoard(con, bno);
			if(bmap != null) {
				ArrayList<Attachment> attList = new BoardDao().selectBoardImages(con, bno);
				int uno = (int)bmap.get("uno");
				HashMap<String, Object> bWritermap  = new BoardDao().selectbWritermap(con, uno);
				ArrayList<HashMap<String, Object>> QnAList = new BoardDao().selectQnAList(con, bno);
				ArrayList<HashMap<String, Object>> reviewList = new BoardDao().selectReviewList(con, bno);
				HashMap<String, Object> rvAttmap = new HashMap<String, Object>();
				for(int i = 0; i < reviewList.size(); i++) {
					int rvno = (int)reviewList.get(i).get("rvNo");
					ArrayList<Attachment> rvAttList = new BoardDao().selectBoardImages(con, rvno);
					rvAttmap.put(String.valueOf(rvno), rvAttList);
				}
				
				bDetailMap.put("bmap", bmap);
				bDetailMap.put("attList", attList);
				bDetailMap.put("bWritermap", bWritermap);
				bDetailMap.put("QnAList", QnAList);
				bDetailMap.put("reviewList", reviewList);
				bDetailMap.put("rvAttmap", rvAttmap);
			}
		}else {
			rollback(con);
		}
		close(con);
		return bDetailMap;
	}

	public int insertQnA(Board newQnA) {
		Connection con = getConnection();
		int result = new BoardDao().insertQnA(con, newQnA);
		if(result > 0) {
			commit(con);
		}else {
			rollback(con);
		}
		close(con);
		return result;
	}

	public ArrayList<HashMap<String, Object>> selectQnAList(int parentBno) {
		Connection con = getConnection();
		ArrayList<HashMap<String, Object>> qnaList = new BoardDao().selectQnAList(con, parentBno);
		close(con);
		return qnaList;
	}

	public int insertReview(Board newReview, ArrayList<Attachment> rvImgList) {
		Connection con = getConnection();
		int result = 0;
		int result1 = new BoardDao().insertReview(con, newReview);
		if(result1 > 0) {
			int bno = new BoardDao().selectCurrval(con);
			for(int i = 0; i < rvImgList.size(); i++) {
				rvImgList.get(i).setBno(bno);
			}
		}
		int result2 = new BoardDao().insertRvImgList(con, rvImgList);
		if(result1 > 0 && result2 == rvImgList.size()) {
			commit(con);
			result = 1;
		}else {
			rollback(con);
			result = 0;
		}
		close(con);
		return result;
	}

	
	
	
	
	
	/*사진게시판 등록용 메소드 ----------------------- 준혁*/
	   
	   public int insertProductBoard(Board b, ArrayList<Attachment> fileList) {
	      Connection con = getConnection();
	      int result = 0;
	      
	      int result1 = new BoardDao().insertThumnailContent(con, b);
	      
	      if(result1 > 0) {
	         int bno = new BoardDao().selectCurrval(con);
	         for(int i = 0; i < fileList.size(); i++) {
	            fileList.get(i).setBno(bno);
	         }
	         
	      }
	      
	      int result2 = new BoardDao().insertAttachment(con, fileList);
	      
	      if(result1 > 0 && result2 == fileList.size()) {
	         commit(con);
	         result = 1;
	      }else {
	         rollback(con);
	         result = 0;
	      }
	      
	      close(con);
	      
	      return result;
	   }

	
	
}














