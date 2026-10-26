import { cn } from "../lib/utils";
import type { Priority, Status } from "../lib/tickets";

const priorityStyles: Record<Priority, string> = {
  Baixa: "bg-green-100 text-green-700",
  Média: "bg-yellow-100 text-yellow-700",
  Alta: "bg-orange-100 text-orange-700",
  Crítica: "bg-red-100 text-red-700",
};

const statusStyles: Record<Status, string> = {
  Aberto: "bg-blue-100 text-blue-700",
  "Em atendimento": "bg-teal-100 text-teal-700",
  "Aguardando resposta": "bg-amber-100 text-amber-700",
  Resolvido: "bg-green-100 text-green-700",
  Fechado: "bg-slate-100 text-slate-700",
};

export function PriorityBadge({ priority }: { priority: Priority }) {
  return (
    <span className={cn("inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-medium", priorityStyles[priority])}>
      <span className="w-1.5 h-1.5 rounded-full bg-current" />
      {priority}
    </span>
  );
}

export function StatusBadge({ status }: { status: Status }) {
  return (
    <span className={cn("inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-medium", statusStyles[status])}>
      <span className="w-1.5 h-1.5 rounded-full bg-current" />
      {status}
    </span>
  );
}