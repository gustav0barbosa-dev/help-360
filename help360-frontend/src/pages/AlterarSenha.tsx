import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft, KeyRound, CheckCircle, AlertCircle } from "lucide-react";
import { AuthAPI } from "../api/api";

export function AlterarSenha() {
  const navigate = useNavigate();
  const [senhaAtual, setSenhaAtual] = useState("");
  const [novaSenha, setNovaSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [carregando, setCarregando] = useState(false);
  const [mensagem, setMensagem] = useState<{ tipo: "sucesso" | "erro"; texto: string } | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setMensagem(null);

    // Validações
    if (novaSenha.length < 8) {
      setMensagem({ tipo: "erro", texto: "A nova senha deve ter no mínimo 8 caracteres." });
      return;
    }
    if (novaSenha !== confirmarSenha) {
      setMensagem({ tipo: "erro", texto: "As senhas não coincidem." });
      return;
    }
    if (novaSenha === senhaAtual) {
      setMensagem({ tipo: "erro", texto: "A nova senha deve ser diferente da atual." });
      return;
    }

    setCarregando(true);
    try {
      await AuthAPI.alterarSenha(senhaAtual, novaSenha);
      setMensagem({ tipo: "sucesso", texto: "Senha alterada com sucesso!" });
      // Limpa os campos após 2 segundos
      setTimeout(() => {
        setSenhaAtual("");
        setNovaSenha("");
        setConfirmarSenha("");
      }, 2000);
    } catch (err: any) {
      setMensagem({ 
        tipo: "erro", 
        texto: err.response?.data?.mensagem || "Erro ao alterar senha." 
      });
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div>
      <button onClick={() => navigate(-1)} className="flex items-center gap-2 text-slate-500 hover:text-slate-800 mb-4">
        <ArrowLeft className="w-4 h-4" /> Voltar
      </button>

      <div className="max-w-2xl mx-auto">
        <div className="bg-white rounded-lg p-6 shadow-sm">
          <div className="flex items-center gap-3 mb-6 pb-4 border-b">
            <div className="bg-teal-100 p-3 rounded-lg">
              <KeyRound className="w-6 h-6 text-teal-700" />
            </div>
            <div>
              <h1 className="text-2xl font-bold">Alterar Senha</h1>
              <p className="text-slate-500 text-sm">Mantenha sua conta segura com uma senha forte</p>
            </div>
          </div>

          {mensagem && (
            <div className={`flex items-start gap-2 p-3 mb-4 rounded-lg ${
              mensagem.tipo === "sucesso" 
                ? "bg-green-50 text-green-700 border border-green-200" 
                : "bg-red-50 text-red-700 border border-red-200"
            }`}>
              {mensagem.tipo === "sucesso" ? <CheckCircle className="w-5 h-5 flex-shrink-0 mt-0.5" /> : <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />}
              <span>{mensagem.texto}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Senha Atual *</label>
              <input
                type="password"
                value={senhaAtual}
                onChange={(e) => setSenhaAtual(e.target.value)}
                required
                placeholder="Digite sua senha atual"
                className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-teal focus:border-transparent outline-none"
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Nova Senha *</label>
              <input
                type="password"
                value={novaSenha}
                onChange={(e) => setNovaSenha(e.target.value)}
                required
                minLength={8}
                placeholder="Mínimo 8 caracteres"
                className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-teal focus:border-transparent outline-none"
              />
              <p className="text-xs text-slate-500 mt-1">A senha deve ter no mínimo 8 caracteres.</p>
            </div>

            <div>
              <label className="block text-sm font-medium text-slate-700 mb-1">Confirmar Nova Senha *</label>
              <input
                type="password"
                value={confirmarSenha}
                onChange={(e) => setConfirmarSenha(e.target.value)}
                required
                placeholder="Digite a nova senha novamente"
                className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-teal focus:border-transparent outline-none"
              />
            </div>

            <div className="flex justify-end gap-2 pt-4 border-t">
              <button
                type="button"
                onClick={() => navigate(-1)}
                className="px-4 py-2 border rounded-lg hover:bg-slate-50"
              >
                Cancelar
              </button>
              <button
                type="submit"
                disabled={carregando}
                className="px-5 py-2 bg-teal text-white rounded-lg hover:bg-teal/90 disabled:opacity-60 font-medium"
              >
                {carregando ? "Salvando..." : "Alterar Senha"}
              </button>
            </div>
          </form>
        </div>

        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mt-4">
          <p className="text-sm text-blue-800">
            <b>Dica de segurança:</b> Use uma senha forte com letras maiúsculas, minúsculas, números e caracteres especiais.
          </p>
        </div>
      </div>
    </div>
  );
}