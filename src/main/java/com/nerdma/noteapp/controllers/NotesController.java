package com.nerdma.noteapp.controllers;

import com.nerdma.noteapp.models.NotesModel;
import com.nerdma.noteapp.models.UserModel;
import com.nerdma.noteapp.repositories.NotesRepository;
import com.nerdma.noteapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;

@RestController
public class NotesController {
    private final UserRepository userRepository;
    private final NotesRepository notesRepository;

    @Autowired
    public NotesController(UserRepository userRepository, NotesRepository notesRepository) {
        this.userRepository = userRepository;
        this.notesRepository = notesRepository;
    }
    @GetMapping("/profile/{username}")
    public RedirectView showProfilePage(@PathVariable String username) {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("/profile.html?username=" + username);
        return redirectView;
    }
    @GetMapping("/api/notes/{username}")
    public List<NotesModel> getUserNotes(@PathVariable String username) {
        UserModel user = userRepository.findByEmail(username);
        if (user == null) {
            return Collections.emptyList();
        }

        return notesRepository.findByUser(user);
    }

    @GetMapping("/edit-note/{id}")
    public ResponseEntity<NotesModel> getNoteDetails(@PathVariable Long id) {
        NotesModel note = notesRepository.findById(id).orElse(null);
        if (note == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(note);
    }

    @PutMapping("/api/notes/{id}")
    public ResponseEntity<NotesModel> updateNote(@PathVariable Long id, @RequestBody NotesModel updatedNote) {
        NotesModel existingNote = notesRepository.findById(id).orElse(null);
        if (existingNote == null) {
            return ResponseEntity.notFound().build();
        }

        existingNote.setNote(updatedNote.getNote());

        NotesModel savedNote = notesRepository.save(existingNote);

        return ResponseEntity.ok().body(savedNote);
    }

    @PostMapping("/api/notes/{username}")
    public ResponseEntity<NotesModel> createNote(@PathVariable String username, @RequestBody NotesModel newNote) {
        UserModel user = userRepository.findByEmail(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        newNote.setUser(user);
        NotesModel savedNote = notesRepository.save(newNote);
        return ResponseEntity.ok().body(savedNote);
    }

    @DeleteMapping("/api/notes/{id}")
    public ResponseEntity<String> deleteNote(@PathVariable Long id) {
        try {
            notesRepository.deleteById(id);
            return ResponseEntity.ok().body("Note deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting note: " + e.getMessage());
        }
    }

}
