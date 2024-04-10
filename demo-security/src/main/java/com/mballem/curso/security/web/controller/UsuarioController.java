package com.mballem.curso.security.web.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("u")
public class UsuarioController {
	@Autowired
	private UsuarioService service;
	@Autowired
	private MedicoService medicoService;
	
	@GetMapping("/novo/cadastro/usuario")
	public String cadastroPorAdminParaAdminMedicoPaciente(Usuario usuario) {
		return "usuario/cadastro";
	}
	@GetMapping("/lista")
	public String listarUsuarios(Usuario usuario) {
		return "usuario/lista";
	}
	
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarDatatables(HttpServletRequest req) {
		return ResponseEntity.ok(service.buscarTodos(req));
	}
	
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
		List<Perfil> perfis = usuario.getPerfis();
		
		
		if(perfis.size() >2 || 
		   perfis.containsAll(Arrays.asList(new Perfil(1L), new Perfil(3L))) ||
		   perfis.containsAll(Arrays.asList(new Perfil(2L), new Perfil(3L)))){
			attr.addFlashAttribute("falha", "Paciente não pode ser Admin e/ou Médico");
			attr.addFlashAttribute("usuario", usuario);
		}
		else {
			try {
			service.salvarUsuario(usuario);
			attr.addFlashAttribute("sucesso","Operação realizada com sucesso!");}
			catch (DataIntegrityViolationException e) {
				attr.addFlashAttribute("falha", "Não é possível cadastrar dois usuários com emails iguais!");
			}
		}
		return "redirect:/u/novo/cadastro/usuario";
	}
	@GetMapping("/editar/credenciais/usuario/{id}")
	public ModelAndView listarDatatables(@PathVariable("id") Long id) {
		return new ModelAndView("usuario/cadastro", "usuario", service.findById(id));
	}
	@GetMapping("/editar/dados/usuario/{id}/perfis/{perfis}")
	public ModelAndView preEditarCadastrosPesoais(@PathVariable("id") Long usuarioId,
			                                      @PathVariable("perfis") Long[] perfis) {
		Usuario usuario = service.findById(usuarioId);
		List<Long> perfil = usuario.getPerfis().stream().map(b-> b.getId()).toList();
		
		return service.editarDeAcordoAoPerfil(perfil, usuario);
		
	}
	@GetMapping("/editar/senha")
	public String abrirEditarSenha() {
		return "usuario/editar-senha";
	}
	@PostMapping("confirmar/senha")
	public String editarSenha(@RequestParam("senha1") String s1, @RequestParam("senha2") String s2,
	                          @RequestParam("senha3") String s3, @AuthenticationPrincipal User user,
	                          RedirectAttributes attr) {
		
		return service.confirmarSenhaEditada(s1,s2,s3,user,attr);
	}
	@GetMapping("/novo/cadastro")
	public String novoCadastro(Usuario usuario) {
		return "cadastrar-se";
	}
	@GetMapping("/cadastro/realizado")
	public String cadastroRealiado() {
		return "fragments/mensagem";
	}
	@PostMapping("/cadastro/paciente/salvar")
	public String salvarCadastroPaciente(Usuario usuario, BindingResult result) throws MessagingException {
		try {
		service.salvarCadastroPaciente(usuario);
		return "redirect:/u/cadastro/realizado";
		}
		catch (DataIntegrityViolationException e) {
			result.reject("email", "ops... Este e-mail já existe na base de dados.");
			return "cadastrar-se";
		}
	}
	@GetMapping("/confirmacao/cadastro")
	public String repostaConfirmacaoCadastroPaciente(@RequestParam("codigo") String codigo,
			                                         RedirectAttributes attr) {
		service.ativarCadastroPaciente(codigo);
		attr.addFlashAttribute("alerta", "sucesso");
		attr.addFlashAttribute("titulo", "Cadastro Ativado!");
		attr.addFlashAttribute("texto", "Parabéns, seu cadastro está ativo.");
		attr.addFlashAttribute("subtexto", "Siga com o seu login/senha");
		return "redirect:/login";
	}
	@GetMapping("/p/redefinir/senha")
	public String pedidoRedefinirSenha() {
		return "usuario/pedido-recuperar-senha";
	}
	@GetMapping("/p/recuperar/senha")
	public String redefinirSenha(String email, ModelMap model) throws MessagingException {
		service.pedidoRedefinirSenha(email);
		model.addAttribute("sucesso", "Em instantes você receberá um e-mail "
				+ "para prosseguir com a redefinição de sua senha.");
		model.addAttribute("usuario", new Usuario(email));
		return "usuario/recuperar-senha";
	}
	@PostMapping("/p/nova/senha/")
	public String confirmacaoDeRedefinicaoDeSenha(Usuario usuario, ModelMap model) {
		Usuario u = service.findByEmail(usuario.getEmail());
		System.out.println(usuario.getCodigoVerificador());
		System.out.println(u.getCodigoVerificador());
		System.out.println(usuario.getCodigoVerificador().equals(u.getCodigoVerificador()));
		if(!usuario.getCodigoVerificador().equals(u.getCodigoVerificador())){
			model.addAttribute("falha", "Código verificador não confere");
			return "usuario/recuperar-senha";
		}
		u.setCodigoVerificador(null);
		service.editarSenha(u, usuario.getPassword());
		model.addAttribute("alerta", "sucesso");
		model.addAttribute("titulo", "Senha redefinida!");
		model.addAttribute("texto", "Você já pode logar no sistema.");
		return "login";
		
	}
}
