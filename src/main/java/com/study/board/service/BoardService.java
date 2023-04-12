package com.study.board.service;

import com.study.board.entity.Board;
import com.study.board.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BoardService {
    @Autowired
    private BoardRepository boardRepository;

    public void write(Board board) throws Exception {
        boardRepository.save(board);
    }

    public void write(Board board, MultipartFile file) throws Exception {
        // 해당 프로젝트 경로 : System.getProperty("user.dir")
        String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files";

        // 랜덤이름 생성
        UUID uuid = UUID.randomUUID();
        String fileName = uuid + "_" + file.getOriginalFilename();

        File saveFile = new File(projectPath, fileName);

        file.transferTo(saveFile);

        board.setFilename(fileName);
        board.setFilepath("/files/" + fileName);

        boardRepository.save(board);
    }

    public List<Board> list() {

        return boardRepository.findAll();
    }

    public Board detail(Integer id) {

        return boardRepository.findById(id).get();
    }

    public void delete(Integer id) {
        boardRepository.deleteById(id);
    }
}
