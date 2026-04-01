//package org.lisovskyi_ivanov.backend.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.lisovskyi_ivanov.backend.entity.Check;
//import org.lisovskyi_ivanov.backend.service.CheckService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/checks")
//@RequiredArgsConstructor
//public class CheckController {
//
//    private final CheckService checkService;
//
//    // GET /api/checks - Отримати список всіх чеків
//    @GetMapping
//    public ResponseEntity<List<Check>> getAllChecks() {
//        List<Check> checks = checkService.findAll();
//        return ResponseEntity.ok(checks);
//    }
//
//    // GET /api/checks/{checkNumber} - Отримати конкретний чек за номером
////    @GetMapping("/{checkNumber}")
////    public ResponseEntity<Check> getCheckById(@PathVariable String checkNumber) {
////        return checkService.findAllByEmployeeId(Long.valueOf(checkNumber))
////                .stream()
////                .map(ResponseEntity::ok)
////                .orElse(ResponseEntity.notFound().build()); // Поверне 404, якщо чек не знайдено
////    }
//
//    // POST /api/checks - Створити новий чек
//    @PostMapping
//    public ResponseEntity<Check> createCheck(@RequestBody Check check) {
//        Check savedCheck = checkService.save(check);
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedCheck); // Поверне 201 Created
//    }
//
//    // PUT /api/checks/{checkNumber} - Оновити існуючий чек
//    @PutMapping("/{checkNumber}")
//    public ResponseEntity<Void> updateCheck(@PathVariable String checkNumber, @RequestBody Check check) {
//        // Зазвичай тут варто переконатися, що ID в URL збігається з ID в тілі запиту
//        checkService.update(check);
//        return ResponseEntity.ok().build();
//    }
//
//    // DELETE /api/checks/{checkNumber} - Видалити чек
//    @DeleteMapping("/{checkNumber}")
//    public ResponseEntity<Void> deleteCheck(@PathVariable String checkNumber) {
//        checkService.deleteById(checkNumber);
//        return ResponseEntity.noContent().build(); // Поверне 204 No Content після успішного видалення
//    }
//}