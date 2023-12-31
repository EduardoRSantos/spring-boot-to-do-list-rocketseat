package org.eduardo.todolist.servicies;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import org.eduardo.todolist.models.TaskModel;
import org.eduardo.todolist.repositories.TaksRepository;
import org.eduardo.todolist.servicies.exceptions.DateTimeException;
import org.eduardo.todolist.servicies.exceptions.NotFoudException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class TaskService {

    @Autowired
    private TaksRepository taskRepository;

    // Esta recuperando o id do usuário pelo request que foi setado no filter.
    public TaskModel create(TaskModel taskModel, HttpServletRequest request) {

        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStarAt()) || currentDate.isAfter(taskModel.getEndAt())) {
            throw new DateTimeException("incorrect date or time");
        }

        if (taskModel.getStarAt().isAfter(taskModel.getEndAt())) {
            throw new DateTimeException("end date is greater than the start date");
        }

        taskModel.setIdUser((UUID) request.getAttribute("idUser"));
        return taskRepository.save(taskModel);
    }

    public List<TaskModel> getAllTasks(HttpServletRequest request) {

        return taskRepository.findByIdUser((UUID) request.getAttribute("idUser"));

    }

    public TaskModel update(UUID id, TaskModel taskNew) {
        var taskOld = taskRepository.findById(id);
        if (taskOld.get() == null) {
            throw new NotFoudException("Taks not found");
        }
        System.out.println(taskOld.get().getCreatedAt());
        updateTask(taskOld.get(), taskNew);

        return taskRepository.save(taskOld.get());

    }

    public static void updateTask(TaskModel taskOld, TaskModel taskNew) {
        taskOld.setDescription(taskNew.getDescription());
        taskOld.setTitle(taskNew.getTitle());
        taskOld.setCreatedAt(taskNew.getCreatedAt());
        taskOld.setEndAt(taskNew.getEndAt());
        taskOld.setPriority(taskNew.getPriority());
        taskOld.setCreatedAt(LocalDateTime.now());
    }
}
