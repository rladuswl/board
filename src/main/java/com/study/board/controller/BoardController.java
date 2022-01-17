package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 게시글 등록 페이지
    @GetMapping("/board/write") //localhost:8080/board/write
    public String boardWriteForm() {

        return "boardwrite";
    }

    // 게시글 등록
    @PostMapping("/board/writepro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception{

        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    // 게시글 리스트 페이지
    @GetMapping("/board/list")
    public String boardList(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable, String searchKeyword) {


        Page<Board> list = null;

        if (searchKeyword == null) {
            list = boardService.boardList(pageable);
        }else {
            list = boardService.boardSearchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardlist";
    }
    // 상세 페이지
    @GetMapping("/board/view") //localhost:8080/board/view?id=1
    public String boardView(Model model, Integer id) {

        model.addAttribute("board", boardService.boardView(id));
        return "boardview";
    }

    // 게시글 삭제
    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {

        boardService.boardDelete(id);
        return "redirect:/board/list";
    }


    //수정 페이지 보여주기
    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {

        model.addAttribute("board", boardService.boardView(id));
        return "boardmodify";
    }

    // 수정 버튼 누른 후 수정한 데이터를 데이터베이스로 전송
    // 여기서 매개변수 Board board 는 수정하기 위해 새로 입력한 내용
    //"글 수정이 완료되었습니다." 메세지 띄우는 코드 추가
    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Board board, Model model, MultipartFile file) throws Exception{

        Board boardTemp = boardService.boardView(id); //객체 만들어서 기존에 있던 데이터(수정 전) 불러오기 -> boardTemp에 기존의 데이터 담김
        boardTemp.setTitle(board.getTitle()); //기존 데이터에다가 수정할 새로운 title 로 덮어씌우기
        boardTemp.setContent(board.getContent()); //기존 데이터에다가 수정할 새로운 content 로 덮어씌우기

        boardService.write(boardTemp, file);

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");


        return "message";
    }

}
