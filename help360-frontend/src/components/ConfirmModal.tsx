import { AlertTriangle } from "lucide-react";

type ConfirmModalProps = {
  aberto: boolean;
  titulo: string;
  mensagem: string;
  textoConfirmar?: string;
  textoCancelar?: string;
  tipo?: "perigo" | "padrao";
  onConfirmar: () => void;
  onCancelar: () => void;
};

export function ConfirmModal({
  aberto,
  titulo,
  mensagem,
  textoConfirmar = "Confirmar",
  textoCancelar = "Cancelar",
  tipo = "padrao",
  onConfirmar,
  onCancelar,
}: ConfirmModalProps) {
  if (!aberto) return null;

  return (
    <div
      className="fixed inset-0 bg-black/60 z-50 flex items-center justify-center p-4"
      onClick={onCancelar}
    >
      <div
        className="bg-white rounded-xl shadow-2xl w-full max-w-md p-6"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex items-start gap-3 mb-4">
          <div className={`p-2 rounded-lg ${tipo === "perigo" ? "bg-red-100" : "bg-yellow-100"}`}>
            <AlertTriangle className={`w-6 h-6 ${tipo === "perigo" ? "text-red-600" : "text-yellow-600"}`} />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-800">{titulo}</h2>
            <p className="text-slate-600 text-sm mt-1">{mensagem}</p>
          </div>
        </div>

        <div className="flex justify-end gap-2 mt-6">
          <button
            onClick={onCancelar}
            className="px-4 py-2 border rounded-lg hover:bg-slate-50 font-medium text-sm"
          >
            {textoCancelar}
          </button>
          <button
            onClick={onConfirmar}
            className={`px-4 py-2 text-white rounded-lg font-medium text-sm ${
              tipo === "perigo" ? "bg-red-600 hover:bg-red-700" : "bg-teal hover:bg-teal/90"
            }`}
          >
            {textoConfirmar}
          </button>
        </div>
      </div>
    </div>
  );
}