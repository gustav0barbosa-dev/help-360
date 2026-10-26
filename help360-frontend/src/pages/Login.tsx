import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { LogIn } from "lucide-react";
import { useAuth } from "../context/AuthContext";

export function Login() {
  const [email, setEmail] = useState("admin@help360.com");
  const [senha, setSenha] = useState("Admin@123");
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setCarregando(true);
    try {
      await login(email, senha);
      navigate("/");
    } catch (err: any) {
      setErro(err.response?.data?.mensagem || "Erro ao fazer login. Verifique suas credenciais.");
    } finally {
      setCarregando(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-[#172B3A] to-[#0F766E] p-4">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-md p-8">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 bg-[#0F766E] rounded-xl mb-4">
            <LogIn className="w-8 h-8 text-white" />
          </div>
          <h1 className="text-2xl font-bold text-slate-800">ServiceDesk TI</h1>
          <p className="text-slate-500 text-sm mt-1">Help360 - Acesso ao sistema</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">E-mail</label>
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)}
              className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-[#0F766E] outline-none" required />
          </div>
          <div>
            <label className="block text-sm font-medium text-slate-700 mb-1">Senha</label>
            <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)}
              className="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-[#0F766E] outline-none" required />
          </div>
          {erro && <div className="bg-red-50 text-red-600 text-sm p-3 rounded-lg border border-red-200">{erro}</div>}
          <button type="submit" disabled={carregando}
            className="w-full bg-[#0F766E] hover:bg-[#0B5F58] text-white font-medium py-2.5 rounded-lg transition disabled:opacity-60">
            {carregando ? "Entrando..." : "Entrar"}
          </button>
        </form>

        <div className="mt-6 text-center text-xs text-slate-400">
          <p>Teste: admin@help360.com / Admin@123</p>
        </div>
      </div>
    </div>
  );
}