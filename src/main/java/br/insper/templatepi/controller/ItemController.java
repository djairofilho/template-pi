package br.insper.templatepi.controller;

import br.insper.templatepi.dto.ItemRequest;
import br.insper.templatepi.dto.ItemResponse;
import br.insper.templatepi.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// TODO(PI): renomeie a rota e ajuste os verbos e códigos conforme o contrato.
@RestController
@RequestMapping("/itens")
public class ItemController {

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	@GetMapping
	public List<ItemResponse> listar(@RequestParam(required = false) String nome) {
		return itemService.listar(nome);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ItemResponse criar(@Valid @RequestBody ItemRequest request) {
		return itemService.criar(request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletar(@PathVariable Long id) {
		itemService.deletar(id);
	}
}
