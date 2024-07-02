package org.finalpjt.hraccoon.domain.todo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.finalpjt.hraccoon.domain.todo.data.dto.TodoRequestDTO;
import org.finalpjt.hraccoon.domain.todo.data.dto.TodoResponseDTO;
import org.finalpjt.hraccoon.domain.todo.data.entity.Todo;
import org.finalpjt.hraccoon.domain.todo.repository.TodoRepository;
import org.finalpjt.hraccoon.domain.user.constant.UserMessageConstants;
import org.finalpjt.hraccoon.domain.user.data.entity.User;
import org.finalpjt.hraccoon.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public List<TodoResponseDTO> findByUserNo(Long userNo) {
        User user = userRepository.findByUserNo(userNo)
			.orElseThrow(() -> new IllegalArgumentException(UserMessageConstants.USER_NOT_FOUND));
        if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 아이디를 가진 유저를 찾지 못했습니다 : " + userNo);
		}
		List<Todo> todoList = todoRepository.findAllByUserNo(user).stream()
			.filter(todo -> !todo.getTodoDeleteYn())
			.collect(Collectors.toList());
            
        List<TodoResponseDTO> responseList = TodoResponseDTO.toDtoList(todoList);
        responseList.forEach(todo -> todo.setUserInfo(user));
        return responseList;
    }

    @Transactional
    public TodoResponseDTO saveTodo(TodoRequestDTO data) {
        User user = userRepository.findByUserNo(data.getUserNo())
			.orElseThrow(() -> new IllegalArgumentException(UserMessageConstants.USER_NOT_FOUND));
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 아이디를 가진 유저를 찾지 못했습니다 : " + data.getUserNo());
		} 
		// (확인필요) 할 일 기입x, 생성버튼 클릭 시 경고창 
		else if(data.getTodoContent() == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "할 일을 기입해주세요");
		}

		Todo todo = data.toEntity(data, user);
		todoRepository.save(todo);

		// 예외 처리
		Todo response = todoRepository.findByTodoNo(todo.getTodoNo());
		if (response == null) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "항목을 생성하는데 실패했습니다.");
		}

        TodoResponseDTO responseDTO = new TodoResponseDTO(response);
		responseDTO.setUserInfo(user);

        return responseDTO;
    }

	@Transactional
	public void completeTodo(Long todoNo) {
		Todo todo = todoRepository.findByTodoNo(todoNo);

		if (todo == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 아이디를 가진 할일을 찾지 못했습니다 : " + todoNo);
		}

		todo.updateTodoCompleteYn();
		todoRepository.save(todo);
	}

	@Transactional
	public void deleteByTodoNo(Long todoNo) {
		Todo todo = todoRepository.findByTodoNo(todoNo);
		if (todo == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 아이디를 가진 할 일을 찾지 못했습니다 : " + todoNo);
		}

		todo.updateTodoDeleteYn();
		todoRepository.save(todo);
	}

}