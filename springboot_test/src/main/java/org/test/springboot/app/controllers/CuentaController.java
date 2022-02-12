package org.test.springboot.app.controllers;

import static org.springframework.http.HttpStatus.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.test.springboot.app.models.Cuenta;
import org.test.springboot.app.models.TransaccionDto;
import org.test.springboot.app.services.CuentaService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/cuentas") //Ruta base Url para acceder a cada uno de los metodos.
public class CuentaController {

    @Autowired
    private CuentaService cuentaService;

    /**
     * Metodos Handler del controlador
     * Metodos manejadores del Request
     * Maneja una solicitud Web un Request HTTP que puede recibir parametros del Usuario.
     */

    @GetMapping
    @ResponseStatus(OK)
    public List<Cuenta> listar() {
        return cuentaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {

        Cuenta cuenta = null;
        try {
            cuenta = cuentaService.findById(id);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cuenta);
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public Cuenta almacenar(@RequestBody Cuenta cuenta) {
        return cuentaService.save(cuenta);
    }

    @PostMapping("/transferencia")
    public ResponseEntity<?> transferencia(@RequestBody TransaccionDto dto) {
        cuentaService.transferir(dto.getCuentaOrigenId(),
                dto.getCuentaDestinoId(), dto.getMonto(), dto.getBancoId());

        Map<String, Object> response = new HashMap<>();
        response.put("date", LocalDate.now().toString());
        response.put("status", "OK");
        response.put("mensaje", "Transferencia realizada con exito!");
        response.put("transaccion", dto);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long id) {
        cuentaService.deleteById(id);
    }

}
