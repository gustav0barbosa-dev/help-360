import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  headers: { "Content-Type": "application/json" },
});

// Adiciona o token em todas as requisições
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("help360_token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// Se receber 401 ou 403, desloga
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 || error.response?.status === 403) {
      localStorage.removeItem("help360_token");
      localStorage.removeItem("help360_usuario");
      window.location.href = "/login";
    }
    return Promise.reject(error);
  }
);

export default api;

// ===== Tipos (opcional, mas ajuda o TypeScript) =====
export type UsuarioAPI = {
  id: number;
  nome: string;
  email: string;
  perfil: string;
  ativo: boolean;
  nomeEmpresa: string | null;
};

export type TicketAPI = {
  id: number;
  titulo: string;
  descricao: string;
  prioridade: "URGENTE" | "MEDIA_2" | "BAIXA_2";
  status: "ABERTO" | "EM_ATENDIMENTO" | "RESOLVIDO" | "FECHADO" | "CANCELADO";
  classificacao: string | null;
  dataCriacao: string;
  previsaoSolucao: string | null;
  dataSolucao: string | null;
  nomeSolicitante: string;
  nomeResponsavel: string | null;
  nomeEmpresa: string;
  nomeArea: string;
  nomeSistema: string;
  nomeCategoria: string;
};

// ===== Endpoints =====
export const AuthAPI = {
  login: (email: string, senha: string) => api.post("/auth/login", { email, senha }),
  alterarSenha: (senhaAtual: string, novaSenha: string) => 
    api.put("/auth/alterar-senha", { senhaAtual, novaSenha }),
};

export const TicketAPI = {
  listar: (params?: any) => api.get("/tickets", { params }),
  listarMeus: () => api.get("/tickets/meus"),
  listarAtivos: () => api.get("/tickets/ativos"),
  buscarPorId: (id: number) => api.get(`/tickets/${id}`),
  criar: (dto: any) => api.post("/tickets", dto),
  fechar: (id: number) => api.patch(`/tickets/${id}/fechar`),
  cancelar: (id: number, justificativa: string) => api.patch(`/tickets/${id}/cancelar`, { justificativa }),
};

export const CadastroAPI = {
  listarEmpresas: () => api.get("/empresas/ativas"),
  listarAreasPorEmpresa: (empresaId: number) => api.get(`/areas/empresa/${empresaId}`),
  listarSistemas: () => api.get("/sistemas"),
  listarCategoriasPorSistema: (sistemaId: number) => api.get(`/categorias/sistema/${sistemaId}`),
  listarAtendentes: () => api.get("/usuarios/atendentes"),
};

export const MensagemAPI = {
  adicionar: (ticketId: number, dto: any) => api.post(`/tickets/${ticketId}/mensagens`, dto),
  listar: (ticketId: number) => api.get(`/tickets/${ticketId}/mensagens`),
};

export const ApontamentoAPI = {
  iniciar: (dto: any) => api.post("/apontamentos", dto),
  finalizar: (id: number) => api.patch(`/apontamentos/${id}/finalizar`),
};

