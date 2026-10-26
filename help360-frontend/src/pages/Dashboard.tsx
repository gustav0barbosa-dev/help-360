import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Plus, List } from "lucide-react";
import { TicketAPI, type TicketAPI as Ticket } from "../api/api";

const STATUS_LABELS: Record<string, string> = {
  ABERTO: "Aberto", EM_ATENDIMENTO: "Em atendimento",
  RESOLVIDO: "Resolvido", FECHADO: "Fechado", CANCELADO: "Cancelado",
};

export function Dashboard() {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    TicketAPI.listar({ pagina: 0, tamanho: 100 })
      .then(r => setTickets(r.data.content || []))
      .catch(() => setTickets([]))
      .finally(() => setCarregando(false));
  }, []);

  if (carregando) return <div>Carregando dashboard...</div>;

  const contar = (s: string) => tickets.filter(t => t.status === s).length;
  const cards = [
    { label: "Abertos", valor: contar("ABERTO"), cor: "border-blue-500", text: "text-blue-600" },
    { label: "Em atendimento", valor: contar("EM_ATENDIMENTO"), cor: "border-teal-500", text: "text-teal-600" },
    { label: "Resolvidos", valor: contar("RESOLVIDO"), cor: "border-green-500", text: "text-green-600" },
    { label: "Total", valor: tickets.length, cor: "border-slate-500", text: "text-slate-600" },
  ];

  return (
    <div>
      <div className="flex justify-between items-start mb-6 flex-wrap gap-3">
        <div>
          <h1 className="text-2xl font-bold">Dashboard</h1>
          <p className="text-slate-500 text-sm">Acompanhamento operacional dos chamados</p>
        </div>
        <div className="flex gap-2">
          <Link to="/tickets" className="px-4 py-2 bg-white border border-slate-300 rounded-lg hover:bg-slate-50 flex items-center gap-2 text-sm font-medium">
            <List className="w-4 h-4" /> Ver todos
          </Link>
          <Link to="/novo-ticket" className="px-4 py-2 bg-[#0F766E] text-white rounded-lg flex items-center gap-2 text-sm font-medium">
            <Plus className="w-4 h-4" /> Novo Ticket
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        {cards.map(c => (
          <div key={c.label} className={`bg-white rounded-lg p-4 border-l-4 ${c.cor} shadow-sm`}>
            <span className="text-slate-500 text-sm">{c.label}</span>
            <b className={`block text-3xl ${c.text}`}>{c.valor}</b>
          </div>
        ))}
      </div>

      <div className="bg-white rounded-lg shadow-sm overflow-hidden">
        <div className="p-4 border-b"><h2 className="font-semibold">Últimos tickets</h2></div>
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-xs font-semibold text-slate-500 bg-slate-50 border-b">
              <th className="p-3">#</th><th className="p-3">Título</th><th className="p-3">Status</th><th className="p-3">Responsável</th>
            </tr>
          </thead>
          <tbody>
            {tickets.slice(0, 5).map(t => (
              <tr key={t.id} className="border-b hover:bg-slate-50">
                <td className="p-3 font-semibold">{t.id}</td>
                <td className="p-3">{t.titulo}</td>
                <td className="p-3">{STATUS_LABELS[t.status] || t.status}</td>
                <td className="p-3">{t.nomeResponsavel || "-"}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}