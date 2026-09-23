package br.insper.templatepi.service;

import br.insper.templatepi.entity.Avaliacao;
import br.insper.templatepi.entity.TipoOperacao;
import br.insper.templatepi.exception.AvaliacaoNaoEncontradaException;
import br.insper.templatepi.observer.AvaliacaoObserver;
import br.insper.templatepi.repository.AvaliacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AvaliacaoService {

	private final AvaliacaoRepository avaliacaoRepository;
	private final List<AvaliacaoObserver> observers;

	public AvaliacaoService(
			AvaliacaoRepository avaliacaoRepository,
			List<AvaliacaoObserver> observers) {
		this.avaliacaoRepository = avaliacaoRepository;
		this.observers = observers;
	}

	@Transactional
	public Avaliacao criar(Avaliacao avaliacao) {
		avaliacao.setId(null);
		avaliacao.setAutor(avaliacao.getAutor().trim());
		avaliacao.setConteudo(avaliacao.getConteudo().trim());

		Avaliacao salva = avaliacaoRepository.save(avaliacao);
		notificarObservers(salva, TipoOperacao.CREATE);
		return salva;
	}

	@Transactional(readOnly = true)
	public List<Avaliacao> listar() {
		return avaliacaoRepository.findAllByOrderByDataAvaliacaoDesc();
	}

	@Transactional(readOnly = true)
	public Avaliacao buscarPorId(Long id) {
		return avaliacaoRepository.findById(id)
				.orElseThrow(() -> new AvaliacaoNaoEncontradaException(id));
	}

	@Transactional
	public void excluir(Long id) {
		Avaliacao avaliacao = buscarPorId(id);
		avaliacaoRepository.delete(avaliacao);
		notificarObservers(avaliacao, TipoOperacao.DELETE);
	}

	private void notificarObservers(Avaliacao avaliacao, TipoOperacao operacao) {
		for (AvaliacaoObserver observer : observers) {
			observer.atualizar(avaliacao, operacao);
		}
	}
}
