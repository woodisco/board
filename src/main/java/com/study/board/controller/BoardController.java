package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.forms.TsvData;
import com.study.board.service.BoardService;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/write")
    public String boardWrite() {

        return "boardWrite";
    }

    @PostMapping("/board/writePro")
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {
        boardService.write(board, file);

        model.addAttribute("message", "글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,
                            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
                            Pageable pageable, String searchKeyword) {

        Page<Board> list = null;

        if(searchKeyword == null) {
            list =  boardService.list(pageable);
        } else {
            list =  boardService.searchList(searchKeyword, pageable);
        }

        int nowPage = list.getPageable().getPageNumber() + 1;
        int startPage = Math.max(nowPage - 4, 1);
        int endPage = Math.min(nowPage + 5, list.getTotalPages());

        model.addAttribute("list", list);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "boardList";
    }

    @GetMapping("/board/detail")
    public String boardDetail(Model model, Integer id) {
        model.addAttribute("board", boardService.detail(id));

        return "boardDetail";
    }

    @GetMapping("/board/delete")
    public String boardDelete(Integer id) {
        boardService.delete(id);

        return "redirect:/board/list";
    }

    @GetMapping("/board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("board", boardService.detail(id));

        return "boardModify";
    }

    @PostMapping("/board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id, Model model, Board board) throws Exception {
        Board boardTmp = boardService.detail(id);
        boardTmp.setTitle(board.getTitle());
        boardTmp.setContent(board.getContent());

        boardService.write(boardTmp);

        model.addAttribute("message", "글 수정이 완료되었습니다.");
        model.addAttribute("searchUrl", "/board/list");

        return "message";
    }

    @GetMapping("/board/upload")
    public String uploadTest() {

        return "uploadTest";
    }



    @PostMapping("/board/uploadPro")
    public String uploadTestPro(MultipartFile file) throws IOException {

        try {
            InputStream inputStream = file.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t");

                TsvData model = new TsvData(columns[0], columns[1], columns[2]);
                System.out.println(model.getNum()+model.getName()+model.getEmail());
                validateModel(model);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/board/upload";
    }

    private void validateModel(TsvData model) {
        System.out.println("==========================validateModel");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<TsvData>> violations = validator.validate(model);
        System.out.println("==========================" + violations);

        String result = "";
        for (ConstraintViolation<TsvData> err : violations) {
            result += err.getMessage() + "<br>";
            System.out.println("==========================" + result);
        }
    }

    @RequestMapping("/board/download")
    public ResponseEntity<String> downloadFile() throws IOException {
        File tsvFile = createTsvFile();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "data.tsv");

        // Return the zipped file as a response
        return new ResponseEntity<String>(headers, HttpStatus.CREATED);
    }


    private File createTsvFile() throws IOException {
        File file = File.createTempFile("data", ".tsv");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write the TSV data
            writer.write("Column1\tColumn2\tColumn3");
            writer.newLine();
            writer.write("Value1\tValue2\tValue3");
        }
        return file;
    }
}