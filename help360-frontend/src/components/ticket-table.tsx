import { useState } from "react";
import { Link } from "react-router-dom";
import { Eye, ChevronLeft, ChevronRight } from "lucide-react";
import { PriorityBadge, StatusBadge } from "./ticket-badges";
import { fmtDate, type Ticket } from "../lib/tickets";

export function TicketTable({ tickets }: { tickets: Ticket[] }) {
  const [pagina, setPagina] = useState(1);
  const [porPagina] = useState(10);

  if (tickets.length === 0) {
    return (
      <div className="text-center py-16 text-slate-500">
        <p className="font-medium text-slate-800">Nenhum ticket encontrado</p>
        <p className="text-sm mt-1">Ajuste os filtros ou crie um novo ticket.</p>
      </div>
    );
  }

  const totalPaginas = Math.ceil(tickets.length / porPagina);
  const inicio = (pagina - 1) * porPagina;
  const ticketsPaginados = tickets.slice(inicio, inicio + porPagina);

  return (
    <div>
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead>
            <tr className="text-left text-xs font-semibold text-slate-500 bg-slate-50 border-b">
              <th className="p-3">#</th>
              <th className="p-3">Prioridade</th>
              <th className="p-3">Título</th>
              <th className="p-3">Sistema</th>
              <th className="p-3">Status</th>
              <th className="p-3">Categoria</th>
              <th className="p-3">Empresa</th>
              <th className="p-3">Responsável</th>
              <th className="p-3">Previsão</th>
              <th className="p-3">Ações</th>
            </tr>
          </thead>
          <tbody>
            {ticketsPaginados.map((t) => (
              <tr key={t.id} className="border-b hover:bg-slate-50">
                <td className="p-3 font-semibold">{t.id}</td>
                <td className="p-3"><PriorityBadge priority={t.priority} /></td>
                <td className="p-3 max-w-xs truncate" title={t.title}>{t.title}</td>
                <td className="p-3">{t.system}</td>
                <td className="p-3"><StatusBadge status={t.status} /></td>
                <td className="p-3">{t.category}</td>
                <td className="p-3">{t.company}</td>
                <td className="p-3">{t.assignee}</td>
                <td className="p-3">{fmtDate(t.dueAt)}</td>
                <td className="p-3">
                  <Link to={`/tickets/${t.id}`} className="inline-flex items-center gap-1 text-teal-700 hover:underline">
                    <Eye className="w-4 h-4" /> Ver
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="flex items-center justify-between p-4 border-t flex-wrap gap-2">
        <span className="text-sm text-slate-500">
          Mostrando {inicio + 1}–{Math.min(inicio + porPagina, tickets.length)} de {tickets.length}
        </span>
        <div className="flex items-center gap-1">
          <button
            onClick={() => setPagina(Math.max(1, pagina - 1))}
            disabled={pagina === 1}
            className="p-2 border rounded-lg disabled:opacity-40 hover:bg-slate-50"
          >
            <ChevronLeft className="w-4 h-4" />
          </button>
          {Array.from({ length: totalPaginas }, (_, i) => i + 1).map((p) => (
            <button
              key={p}
              onClick={() => setPagina(p)}
              className={`w-8 h-8 rounded-lg text-sm font-medium ${p === pagina ? "bg-teal text-white" : "border hover:bg-slate-50"}`}
            >
              {p}
            </button>
          ))}
          <button
            onClick={() => setPagina(Math.min(totalPaginas, pagina + 1))}
            disabled={pagina === totalPaginas}
            className="p-2 border rounded-lg disabled:opacity-40 hover:bg-slate-50"
          >
            <ChevronRight className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
}