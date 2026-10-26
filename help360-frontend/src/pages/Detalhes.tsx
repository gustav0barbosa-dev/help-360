import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { ArrowLeft, MessageSquare } from "lucide-react";
import { TicketAPI, MensagemAPI, ApontamentoAPI, type TicketAPI as Ticket } from "../api/api";
import { ConfirmModal } from "../components/ConfirmModal";

const STATUS_LABELS: Record<string, string> = {
  ABERTO: "Aberto", EM_ATENDIMENTO: "Em atendimento",
  RESOLVIDO: "Resolvido", FECHADO: "Fechado", CANCELADO: "Cancelado",
};

export function Detalhes() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [ticket, setTicket] = useState<Ticket | null>(null);
  const [mensagens, setMensagens] = useState<any[]>([]); // <-- Corrigido
  const [novaMsg, setNovaMsg] = useState("");
  const [publico, setPublico] = useState(true);
  const [carregando, setCarregando] = useState(true);
  const [modalFecharAberto, setModalFecharAberto] = useState(false); // <-- Modal aqui

  useEffect(() => {
    if (!id) return;
    Promise.all([
      TicketAPI.buscarPorId(Number(id)),
      MensagemAPI.listar(Number(id)).catch(() => ({ data: [] })),
    ]).then(([t, m]) => {
      setTicket(t.data);
      setMensagens(m.data);
    }).catch(() => {}).finally(() => setCarregando(false));
  }, [id]);

  async function enviarMensagem(e: React.FormEvent) {
    e.preventDefault();
    if (!novaMsg.trim() || !id) return;
    try {
      const { data } = await MensagemAPI.adicionar(Number(id), { texto: novaMsg, publico });
      setMensagens([...mensagens, data]);
      setNovaMsg("");
    } catch (err: any) {
      alert(err.response?.data?.mensagem || "Erro ao enviar mensagem.");
    }
  }

  async function fecharTicket() {
    if (!id) return;
    try {
      const { data } = await TicketAPI.fechar(Number(id));
      setTicket(data);
      setModalFecharAberto(false);
    } catch (err: any) {
      alert(err.response?.data?.mensagem || "Erro ao fechar ticket.");
    }
  }

  async function iniciarTrabalho() {
    if (!id) return;
    try {
      await ApontamentoAPI.iniciar({ ticketId: Number(id), tipoTrabalho: "TICKET", descricao: "Atendimento iniciado" });
      alert("Apontamento iniciado!");
    } catch (err: any) {
      alert(err.response?.data?.mensagem || "Erro ao iniciar apontamento.");
    }
  }

  if (carregando) return <div className="p-8">Carregando...</div>;
  if (!ticket) return <div className="p-8">Ticket não encontrado.</div>;

  return (
    <div>
      <button onClick={() => navigate(-1)} className="flex items-center gap-2 text-slate-500 hover:text-slate-800 mb-4">
        <ArrowLeft className="w-4 h-4" /> Voltar
      </button>

      <div className="bg-white rounded-lg p-6 shadow-sm mb-4">
        <div className="flex justify-between items-start gap-4 mb-4 flex-wrap">
          <div>
            <p className="text-slate-500 text-sm">Ticket #{ticket.id}</p>
            <h1 className="text-2xl font-bold mt-1">{ticket.titulo}</h1>
            <div className="flex gap-2 mt-3">
              <span className="px-2.5 py-0.5 rounded-full text-xs bg-slate-100">{ticket.prioridade}</span>
              <span className="px-2.5 py-0.5 rounded-full text-xs bg-blue-100 text-blue-700">{STATUS_LABELS[ticket.status]}</span>
            </div>
          </div>
          <div className="flex gap-2">
            <button onClick={iniciarTrabalho} className="px-4 py-2 bg-teal text-white rounded-lg text-sm font-medium">Iniciar trabalho</button>
            {ticket.status !== "FECHADO" && ticket.status !== "CANCELADO" && (
              <button onClick={() => setModalFecharAberto(true)} className="px-4 py-2 border rounded-lg text-sm font-medium hover:bg-slate-50">
                Fechar ticket
              </button>
            )}
          </div>
        </div>

        <p className="text-slate-700 mb-6">{ticket.descricao}</p>

        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 text-sm border-t pt-4">
          <div><span className="text-slate-500 text-xs">Solicitante</span><br/><b>{ticket.nomeSolicitante}</b></div>
          <div><span className="text-slate-500 text-xs">Responsável</span><br/><b>{ticket.nomeResponsavel || "-"}</b></div>
          <div><span className="text-slate-500 text-xs">Empresa</span><br/><b>{ticket.nomeEmpresa}</b></div>
          <div><span className="text-slate-500 text-xs">Sistema</span><br/><b>{ticket.nomeSistema}</b></div>
          <div><span className="text-slate-500 text-xs">Categoria</span><br/><b>{ticket.nomeCategoria}</b></div>
          <div><span className="text-slate-500 text-xs">Área</span><br/><b>{ticket.nomeArea}</b></div>
          <div><span className="text-slate-500 text-xs">Criado em</span><br/><b>{ticket.dataCriacao?.slice(0, 16).replace("T", " ")}</b></div>
          <div><span className="text-slate-500 text-xs">Previsão</span><br/><b>{ticket.previsaoSolucao?.slice(0, 16).replace("T", " ") || "-"}</b></div>
        </div>
      </div>

      <div className="bg-white rounded-lg p-6 shadow-sm">
        <h2 className="font-semibold mb-4 flex items-center gap-2">
          <MessageSquare className="w-4 h-4" /> Histórico ({mensagens.length})
        </h2>

        <div className="space-y-3 mb-4">
          {mensagens.map((m: any) => (
            <div key={m.id} className={`p-3 rounded-lg border ${m.publico ? "bg-white" : "bg-yellow-50 border-yellow-200"}`}>
              <div className="text-xs text-slate-500 mb-1">
                <b>{m.nomeAutor}</b> · {m.dataEnvio?.slice(0, 16).replace("T", " ")}
                {!m.publico && <span className="ml-2 text-yellow-700 font-medium">(Interno)</span>}
              </div>
              <p>{m.texto}</p>
            </div>
          ))}
          {mensagens.length === 0 && <p className="text-slate-500 text-sm">Nenhuma mensagem ainda.</p>}
        </div>

        <form onSubmit={enviarMensagem} className="border-t pt-4">
          <textarea value={novaMsg} onChange={(e) => setNovaMsg(e.target.value)} placeholder="Adicionar comentário..." rows={2} className="w-full px-3 py-2 border rounded-lg" />
          <div className="flex justify-between items-center mt-2">
            <label className="flex items-center gap-2 text-sm text-slate-600">
              <input type="checkbox" checked={publico} onChange={(e) => setPublico(e.target.checked)} /> Comentário público
            </label>
            <button type="submit" className="px-4 py-2 bg-teal text-white rounded-lg font-medium">Enviar</button>
          </div>
        </form>
      </div>

      {/* Modal de confirmação */}
      <ConfirmModal
        aberto={modalFecharAberto}
        titulo="Fechar Ticket"
        mensagem="Tem certeza que deseja fechar este ticket? Esta ação não pode ser desfeita."
        textoConfirmar="Sim, fechar ticket"
        textoCancelar="Cancelar"
        tipo="perigo"
        onConfirmar={fecharTicket}
        onCancelar={() => setModalFecharAberto(false)}
      />
    </div>
  );
}