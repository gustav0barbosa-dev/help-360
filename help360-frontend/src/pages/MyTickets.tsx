import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Plus } from "lucide-react";
import { TicketAPI, type TicketAPI as Ticket } from "../api/api";

const STATUS_LABELS: Record<string, string> = {
  ABERTO: "Aberto", EM_ATENDIMENTO: "Em atendimento",
  RESOLVIDO: "Resolvido", FECHADO: "Fechado", CANCELADO: "Cancelado",
};
const PRIORIDADE_LABELS: Record<string, string> = {
  URGENTE: "Urgente", MEDIA_2: "Média", BAIXA_2: "Baixa",
};

export function MyTickets() {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    TicketAPI.listarMeus()
      .then((r) => setTickets(r.data || []))
      .catch(() => setTickets([]))
      .finally(() => setCarregando(false));
  }, []);

  if (carregando) return <div className="p-8">Carregando...</div>;

  return (
    <div>
      <div className="flex justify-between items-start mb-6 flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-bold">Meus Tickets</h1>
          <p className="text-slate-500 text-sm">Acompanhe os chamados sob sua responsabilidade</p>
        </div>
        <Link to="/novo-ticket" className="px-4 py-2 bg-[#0F766E] text-white rounded-lg flex items-center gap-2 text-sm font-medium">
          <Plus className="w-4 h-4" /> Novo Ticket
        </Link>
      </div>

      <div className="bg-white rounded-lg shadow-sm overflow-hidden">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-xs font-semibold text-slate-500 bg-slate-50 border-b">
              <th className="p-3">#</th>
              <th className="p-3">Prioridade</th>
              <th className="p-3">Título</th>
              <th className="p-3">Status</th>
              <th className="p-3">Sistema</th>
              <th className="p-3">Ações</th>
            </tr>
          </thead>
          <tbody>
            {tickets.map((t) => (
              <tr key={t.id} className="border-b hover:bg-slate-50">
                <td className="p-3 font-semibold">{t.id}</td>
                <td className="p-3">{PRIORIDADE_LABELS[t.prioridade]}</td>
                <td className="p-3 max-w-xs truncate" title={t.titulo}>{t.titulo}</td>
                <td className="p-3">{STATUS_LABELS[t.status]}</td>
                <td className="p-3">{t.nomeSistema}</td>
                <td className="p-3">
                  <Link to={`/tickets/${t.id}`} className="text-teal-700 hover:underline">Ver</Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {tickets.length === 0 && <div className="p-8 text-center text-slate-500">Nenhum ticket atribuído a você.</div>}
      </div>
    </div>
  );
}