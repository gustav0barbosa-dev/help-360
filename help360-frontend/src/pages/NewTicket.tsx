import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { TicketAPI, CadastroAPI } from "../api/api";

export function NewTicket() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    titulo: "", descricao: "", prioridade: "MEDIA_2",
    empresaId: "", areaId: "", sistemaId: "", categoriaId: "",
    responsavelId: "", mensagemInicial: "", publico: true,
  });
  const [empresas, setEmpresas] = useState<any[]>([]);
  const [areas, setAreas] = useState<any[]>([]);
  const [sistemas, setSistemas] = useState<any[]>([]);
  const [categorias, setCategorias] = useState<any[]>([]);
  const [atendentes, setAtendentes] = useState<any[]>([]);
  const [erro, setErro] = useState("");
  const [salvando, setSalvando] = useState(false);

  useEffect(() => {
    CadastroAPI.listarEmpresas().then(r => setEmpresas(r.data)).catch(() => {});
    CadastroAPI.listarSistemas().then(r => setSistemas(r.data)).catch(() => {});
    CadastroAPI.listarAtendentes().then(r => setAtendentes(r.data)).catch(() => {});
  }, []);

  useEffect(() => {
    if (form.empresaId) CadastroAPI.listarAreasPorEmpresa(Number(form.empresaId)).then(r => setAreas(r.data)).catch(() => setAreas([]));
  }, [form.empresaId]);

  useEffect(() => {
    if (form.sistemaId) CadastroAPI.listarCategoriasPorSistema(Number(form.sistemaId)).then(r => setCategorias(r.data)).catch(() => setCategorias([]));
  }, [form.sistemaId]);

  const update = (k: string, v: any) => setForm(p => ({ ...p, [k]: v }));

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setErro("");
    setSalvando(true);
    try {
      await TicketAPI.criar({
        ...form,
        empresaId: Number(form.empresaId),
        areaId: Number(form.areaId),
        sistemaId: Number(form.sistemaId),
        categoriaId: Number(form.categoriaId),
        responsavelId: form.responsavelId ? Number(form.responsavelId) : null,
      });
      navigate("/tickets");
    } catch (err: any) {
      setErro(err.response?.data?.mensagem || "Erro ao criar ticket.");
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div>
      <h1 className="text-2xl font-bold mb-6">Novo Ticket</h1>
      <form onSubmit={submit} className="bg-white rounded-lg p-6 shadow-sm space-y-6">
        {erro && <div className="bg-red-50 text-red-600 p-3 rounded-lg">{erro}</div>}

        <section>
          <h2 className="font-semibold mb-3 pb-2 border-b">1. Informações</h2>
          <div className="grid md:grid-cols-2 gap-4">
            <div className="md:col-span-2">
              <label className="block text-sm font-medium mb-1">Título *</label>
              <input required value={form.titulo} onChange={e => update("titulo", e.target.value)} className="w-full px-3 py-2 border rounded-lg" />
            </div>
            <div className="md:col-span-2">
              <label className="block text-sm font-medium mb-1">Descrição *</label>
              <textarea required rows={4} value={form.descricao} onChange={e => update("descricao", e.target.value)} className="w-full px-3 py-2 border rounded-lg" />
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Prioridade *</label>
              <select required value={form.prioridade} onChange={e => update("prioridade", e.target.value)} className="w-full px-3 py-2 border rounded-lg">
                <option value="URGENTE">Urgente (3h)</option>
                <option value="MEDIA_2">Média (9 dias)</option>
                <option value="BAIXA_2">Baixa (27 dias)</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Empresa *</label>
              <select required value={form.empresaId} onChange={e => update("empresaId", e.target.value)} className="w-full px-3 py-2 border rounded-lg">
                <option value="">Selecione</option>
                {empresas.map((e: any) => <option key={e.id} value={e.id}>{e.nome}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Área *</label>
              <select required value={form.areaId} onChange={e => update("areaId", e.target.value)} disabled={!form.empresaId} className="w-full px-3 py-2 border rounded-lg disabled:bg-slate-100">
                <option value="">Selecione</option>
                {areas.map((a: any) => <option key={a.id} value={a.id}>{a.nome}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Sistema *</label>
              <select required value={form.sistemaId} onChange={e => update("sistemaId", e.target.value)} className="w-full px-3 py-2 border rounded-lg">
                <option value="">Selecione</option>
                {sistemas.map((s: any) => <option key={s.id} value={s.id}>{s.nome}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Categoria *</label>
              <select required value={form.categoriaId} onChange={e => update("categoriaId", e.target.value)} disabled={!form.sistemaId} className="w-full px-3 py-2 border rounded-lg disabled:bg-slate-100">
                <option value="">Selecione</option>
                {categorias.map((c: any) => <option key={c.id} value={c.id}>{c.nome}</option>)}
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium mb-1">Responsável</label>
              <select value={form.responsavelId} onChange={e => update("responsavelId", e.target.value)} className="w-full px-3 py-2 border rounded-lg">
                <option value="">Nenhum</option>
                {atendentes.map((a: any) => <option key={a.id} value={a.id}>{a.nome}</option>)}
              </select>
            </div>
          </div>
        </section>

        <section>
          <h2 className="font-semibold mb-3 pb-2 border-b">2. Mensagem inicial</h2>
          <textarea rows={3} value={form.mensagemInicial} onChange={e => update("mensagemInicial", e.target.value)} placeholder="Detalhes adicionais..." className="w-full px-3 py-2 border rounded-lg" />
          <label className="flex items-center gap-2 mt-3 text-sm">
            <input type="checkbox" checked={form.publico} onChange={e => update("publico", e.target.checked)} />
            Comentário público
          </label>
        </section>

        <div className="flex justify-end gap-2 pt-4 border-t">
          <button type="button" onClick={() => navigate("/tickets")} className="px-4 py-2 border rounded-lg hover:bg-slate-50">Cancelar</button>
          <button type="submit" disabled={salvando} className="px-5 py-2 bg-[#0F766E] text-white rounded-lg hover:bg-[#0B5F58] disabled:opacity-60">
            {salvando ? "Criando..." : "Criar Ticket"}
          </button>
        </div>
      </form>
    </div>
  );
}