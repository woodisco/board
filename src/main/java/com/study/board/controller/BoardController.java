package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.util.*;
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

        if (searchKeyword == null) {
            list = boardService.list(pageable);
        } else {
            list = boardService.searchList(searchKeyword, pageable);
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


    //@PostMapping("/board/uploadPro")
    @RequestMapping(value = "/board/uploadPro", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadTestPro(@RequestParam("file") MultipartFile file, Model model) throws IOException {


        Map<String, Object> tesst = new HashMap<>();
        try {
//            if(file.isEmpty()) {
//                model.addAttribute("errorMessage3", "파일없음");
//                return "/uploadTest";
//            }

            InputStream inputStream = file.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t");
                List<String> list = new ArrayList<>();
                list.add(Arrays.toString(columns));

                download(list);
            }
            tesst.put("result", "OK");
            tesst.put("message", "asdasd");
            //List<String> list1 = new ArrayList<>();
            //list1.add("asd");
            //download(list1, response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tesst;
    }

    public void download(List<String> list) throws IOException {
        String tsvContent = list.toString();
        String fileName = "error.tsv";

        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(zipOutputStream)) {
            ZipEntry entry = new ZipEntry(fileName);
            zip.putNextEntry(entry);
            zip.write(tsvContent.getBytes());
            zip.closeEntry();
        }

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=download.zip");

        try (OutputStream out = response.getOutputStream()) {
            out.write(zipOutputStream.toByteArray());
            out.flush();
        }
    }

//    @RequestMapping("/board/download")
//    public ResponseEntity<InputStreamResource> download() throws IOException {
//
//        List<String> list1 = new ArrayList<>();
//        list1.add("asd1");
//        list1.add("asd2");
//        list1.add("asd3");
//        list1.add("asd4");
//        list1.add("asd5");
//
//        // Create TSV content
//        StringBuilder tsvContentBuilder = new StringBuilder();
//        for (int i = 0; i < list1.size(); i++) {
//            tsvContentBuilder.append(list1.get(i));
//            if ((i + 1) % 3 == 0) {
//                tsvContentBuilder.append("\n"); // Add a newline after every third entry
//            } else {
//                tsvContentBuilder.append("\t"); // Add a tab between entries
//            }
//        }
//
//        String tsvContent = tsvContentBuilder.toString();
//        String fileName = "error.tsv";
//
//        ByteArrayOutputStream zipOutputStream = new ByteArrayOutputStream();
//        try (ZipOutputStream zip = new ZipOutputStream(zipOutputStream)) {
//            ZipEntry entry = new ZipEntry(fileName);
//            zip.putNextEntry(entry);
//            zip.write(tsvContent.getBytes());
//            zip.closeEntry();
//        }
//
//        ByteArrayInputStream zipInputStream = new ByteArrayInputStream(zipOutputStream.toByteArray());
//        InputStreamResource inputStreamResource = new InputStreamResource(zipInputStream);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.setContentDisposition(ContentDisposition.attachment().filename("download.zip").build());
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(inputStreamResource);
//    }

    @RequestMapping("/board/password")
    public String passwordTest(@RequestParam("password") String password, RedirectAttributes redirectAttributes) throws Exception {
        String test = "test";
        String errorMessage = "test error";
        if (!password.equals(test)) {
            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
            System.out.println("===================errorMessage");
        }
        return "redirect:/board/upload";
    }
}