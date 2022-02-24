package com.example.algamoney.api.service;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.algamoney.api.model.Lancamento;
import com.example.algamoney.api.model.Pessoa;
import com.example.algamoney.api.repository.LancamentoRepository;
import com.example.algamoney.api.repository.PessoaRepository;
import com.example.algamoney.api.service.exception.PessoaInexistenteOuInativaException;

@Service // serviço Spring
public class LancamentoService {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired 
	private LancamentoRepository lancamentoRepository;
	
	// Regra para não salvar pessoa inativa, lançando a exceção
	 public Lancamento salvar(@Valid Lancamento lancamento) throws PessoaInexistenteOuInativaException {
	        Optional<Pessoa> pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());

	        if (!pessoa.isPresent() || pessoa.get().isInativo()) {
	            throw new PessoaInexistenteOuInativaException();
	        }

	        return lancamentoRepository.save(lancamento);
	    }
	 
	 public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		 Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		 if(!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())){
			 validarPessoa(lancamento);
		 }
		 
		 BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");
		 
		 return lancamentoRepository.save(lancamentoSalvo);
	 }	

	private void validarPessoa(Lancamento lancamento) {
		Optional<Pessoa> pessoa = null;
		if (lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		}

		if (pessoa.isEmpty() || pessoa.get().isInativo()) {
			throw new PessoaInexistenteOuInativaException();
		}
		
	}
	
	private Lancamento buscarLancamentoExistente(Long codigo) {
		/* 		Optional<Lancamento> lancamentoSalvo = lancamentoRepository.findById(codigo);
		if (lancamentoSalvo.isEmpty()) {
			throw new IllegalArgumentException();
		} */
		return lancamentoRepository.findById(codigo).orElseThrow(() -> new IllegalArgumentException());
	}
	
	

}
