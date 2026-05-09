import React, { useEffect, useState, useCallback } from "react";
import {
  PieChart, Pie, Cell,
  BarChart, Bar, XAxis, YAxis, Tooltip,
  ResponsiveContainer,
} from "recharts";
import { Category, Transaction } from "../types";
import {
  getCategories, createCategory, deleteCategory,
  getTransactions, createTransaction, deleteTransaction,
} from "../api/api";

interface Props { onLogout: () => void; }

const COLORS = ["#3b82f6","#10b981","#f59e0b","#ef4444","#8b5cf6","#06b6d4","#f97316","#84cc16"];
const card   = { background: "#fff", borderRadius: 12, padding: "20px 24px", boxShadow: "0 2px 8px rgba(0,0,0,.08)" } as const;

const Dashboard: React.FC<Props> = ({ onLogout }) => {
  const [categories,   setCategories]   = useState<Category[]>([]);
  const [selectedCat,  setSelectedCat]  = useState<Category | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [loading,      setLoading]      = useState(true);
  const [error,        setError]        = useState("");

  const [newCat, setNewCat] = useState({ title: "", description: "" });
  const [newTx,  setNewTx]  = useState({ amount: "", note: "" });
  const [catErr, setCatErr] = useState("");
  const [txErr,  setTxErr]  = useState("");

  // ── Loaders ───────────────────────────────────────────────────────────────
  const loadCategories = useCallback(async () => {
    try {
      const res = await getCategories();
      setCategories(res.data);
    } catch (e: any) {
      setError(e.response?.data?.message || "Failed to load categories");
    } finally {
      setLoading(false);
    }
  }, []);

  const loadTransactions = useCallback(async (cat: Category) => {
    setSelectedCat(cat);
    setTransactions([]);
    try {
      const res = await getTransactions(cat.categoryId);
      setTransactions(res.data);
    } catch {
      setTransactions([]);
    }
  }, []);

  useEffect(() => { loadCategories(); }, [loadCategories]);

  // ── Actions ───────────────────────────────────────────────────────────────
  const addCategory = async (e: React.FormEvent) => {
    e.preventDefault();
    setCatErr("");
    try {
      await createCategory(newCat.title, newCat.description);
      setNewCat({ title: "", description: "" });
      await loadCategories();
    } catch (e: any) {
      setCatErr(e.response?.data?.message || "Failed to create category");
    }
  };

  const removeCategory = async (id: number) => {
    try {
      await deleteCategory(id);
      if (selectedCat?.categoryId === id) { setSelectedCat(null); setTransactions([]); }
      await loadCategories();
    } catch {
      setError("Failed to delete category");
    }
  };

  const addTransaction = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedCat) return;
    setTxErr("");
    try {
      await createTransaction(selectedCat.categoryId, parseFloat(newTx.amount), newTx.note, Date.now());
      setNewTx({ amount: "", note: "" });
      await loadTransactions(selectedCat);
      await loadCategories();         // refresh totalExpense
    } catch (e: any) {
      setTxErr(e.response?.data?.message || "Failed to add transaction");
    }
  };

  const removeTransaction = async (txId: number) => {
    if (!selectedCat) return;
    try {
      await deleteTransaction(selectedCat.categoryId, txId);
      await loadTransactions(selectedCat);
      await loadCategories();
    } catch {
      setTxErr("Failed to delete transaction");
    }
  };

  // ── Derived ───────────────────────────────────────────────────────────────
  const totalExpense = categories.reduce((s, c) => s + c.totalExpense, 0);
  const pieData = categories.filter(c => c.totalExpense > 0).map(c => ({ name: c.title, value: c.totalExpense }));
  const barData = categories.map(c => ({ name: c.title, expense: c.totalExpense }));

  if (loading) return (
    <div style={{ display:"flex", height:"100vh", alignItems:"center", justifyContent:"center", background:"#f0f4f8" }}>
      <p style={{ color:"#666", fontSize:16 }}>Loading your dashboard…</p>
    </div>
  );

  // ── Input style helper ────────────────────────────────────────────────────
  const inp: React.CSSProperties = { padding:"8px 12px", border:"1px solid #ddd", borderRadius:8, fontSize:13, boxSizing:"border-box" };
  const btn = (bg: string): React.CSSProperties => ({ background:bg, color:"#fff", border:"none", padding:"8px 16px", borderRadius:8, cursor:"pointer", fontSize:13, fontWeight:600 });

  return (
    <div style={{ minHeight:"100vh", background:"#f0f4f8", fontFamily:"system-ui,sans-serif" }}>
      {/* Header */}
      <div style={{ background:"#1e3a5f", color:"#fff", padding:"16px 32px", display:"flex", justifyContent:"space-between", alignItems:"center" }}>
        <h2 style={{ margin:0 }}>💰 Expense Tracker v2</h2>
        <button onClick={onLogout} style={{ background:"rgba(255,255,255,.15)", color:"#fff", border:"1px solid rgba(255,255,255,.3)", padding:"8px 18px", borderRadius:8, cursor:"pointer" }}>
          Logout
        </button>
      </div>

      <div style={{ padding:32, maxWidth:1200, margin:"0 auto" }}>
        {error && <div style={{ background:"#fee2e2", color:"#dc2626", padding:"12px 16px", borderRadius:8, marginBottom:20 }}>{error}</div>}

        {/* Stat cards */}
        <div style={{ display:"grid", gridTemplateColumns:"repeat(3,1fr)", gap:20, marginBottom:32 }}>
          {[
            { label:"Total Expense",  value:`₹${totalExpense.toFixed(2)}`, color:"#ef4444" },
            { label:"Categories",     value:categories.length,             color:"#3b82f6" },
            { label:"Transactions",   value:transactions.length,           color:"#10b981" },
          ].map(s => (
            <div key={s.label} style={card}>
              <p style={{ color:"#666", margin:"0 0 8px", fontSize:13 }}>{s.label}</p>
              <p style={{ color:s.color, fontSize:28, fontWeight:700, margin:0 }}>{s.value}</p>
            </div>
          ))}
        </div>

        {/* Charts */}
        {pieData.length > 0 && (
          <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:20, marginBottom:32 }}>
            <div style={card}>
              <h3 style={{ margin:"0 0 16px", color:"#1e3a5f" }}>Spending Breakdown</h3>
              <ResponsiveContainer width="100%" height={220}>
                <PieChart>
                  <Pie data={pieData} cx="50%" cy="50%" outerRadius={80} dataKey="value"
                    label={({ name, percent }) => `${name} ${(percent*100).toFixed(0)}%`}>
                    {pieData.map((_, i) => <Cell key={i} fill={COLORS[i % COLORS.length]} />)}
                  </Pie>
                  <Tooltip formatter={(v: any) => `₹${Number(v).toFixed(2)}`} />
                </PieChart>
              </ResponsiveContainer>
            </div>
            <div style={card}>
              <h3 style={{ margin:"0 0 16px", color:"#1e3a5f" }}>Category Comparison</h3>
              <ResponsiveContainer width="100%" height={220}>
                <BarChart data={barData}>
                  <XAxis dataKey="name" tick={{ fontSize:11 }} />
                  <YAxis tick={{ fontSize:11 }} />
                  <Tooltip formatter={(v: any) => `₹${Number(v).toFixed(2)}`} />
                  <Bar dataKey="expense" fill="#3b82f6" radius={[4,4,0,0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:20 }}>
          {/* Category panel */}
          <div style={card}>
            <h3 style={{ margin:"0 0 16px", color:"#1e3a5f" }}>Categories</h3>
            {catErr && <p style={{ color:"#dc2626", fontSize:13, marginBottom:8 }}>{catErr}</p>}
            <form onSubmit={addCategory} style={{ display:"flex", gap:8, marginBottom:16 }}>
              <input placeholder="Title" value={newCat.title}
                onChange={e => setNewCat({ ...newCat, title:e.target.value })} required
                style={{ ...inp, flex:1 }} />
              <input placeholder="Description" value={newCat.description}
                onChange={e => setNewCat({ ...newCat, description:e.target.value })} required
                style={{ ...inp, flex:1 }} />
              <button type="submit" style={btn("#1e3a5f")}>+ Add</button>
            </form>
            <div style={{ maxHeight:320, overflowY:"auto" }}>
              {categories.length === 0 && <p style={{ color:"#aaa", textAlign:"center", marginTop:30 }}>No categories yet — add one above!</p>}
              {categories.map((cat, i) => (
                <div key={cat.categoryId} onClick={() => loadTransactions(cat)}
                  style={{ display:"flex", justifyContent:"space-between", alignItems:"center",
                    padding:"10px 12px", borderRadius:8, marginBottom:6, cursor:"pointer",
                    background: selectedCat?.categoryId === cat.categoryId ? "#eff6ff" : "#f8fafc",
                    border: selectedCat?.categoryId === cat.categoryId ? "2px solid #3b82f6" : "1px solid #e2e8f0" }}>
                  <div style={{ display:"flex", alignItems:"center", gap:10 }}>
                    <div style={{ width:12, height:12, borderRadius:"50%", background:COLORS[i % COLORS.length], flexShrink:0 }} />
                    <div>
                      <p style={{ margin:0, fontWeight:500, fontSize:14 }}>{cat.title}</p>
                      <p style={{ margin:0, color:"#888", fontSize:11 }}>{cat.description}</p>
                    </div>
                  </div>
                  <div style={{ display:"flex", alignItems:"center", gap:10 }}>
                    <span style={{ color:"#ef4444", fontWeight:700, fontSize:13 }}>₹{cat.totalExpense.toFixed(2)}</span>
                    <button onClick={e => { e.stopPropagation(); removeCategory(cat.categoryId); }}
                      style={{ background:"#fee2e2", color:"#dc2626", border:"none", borderRadius:6, padding:"3px 8px", cursor:"pointer", fontSize:12 }}>✕</button>
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Transaction panel */}
          <div style={card}>
            <h3 style={{ margin:"0 0 16px", color:"#1e3a5f" }}>
              {selectedCat ? `Transactions — ${selectedCat.title}` : "← Select a category"}
            </h3>
            {txErr && <p style={{ color:"#dc2626", fontSize:13, marginBottom:8 }}>{txErr}</p>}
            {selectedCat && (
              <form onSubmit={addTransaction} style={{ display:"flex", gap:8, marginBottom:16 }}>
                <input type="number" placeholder="Amount" value={newTx.amount} step="0.01"
                  onChange={e => setNewTx({ ...newTx, amount:e.target.value })} required
                  style={{ ...inp, width:90 }} />
                <input placeholder="Note" value={newTx.note}
                  onChange={e => setNewTx({ ...newTx, note:e.target.value })} required
                  style={{ ...inp, flex:1 }} />
                <button type="submit" style={btn("#10b981")}>+ Add</button>
              </form>
            )}
            <div style={{ maxHeight:340, overflowY:"auto" }}>
              {selectedCat && transactions.length === 0 &&
                <p style={{ color:"#aaa", textAlign:"center", marginTop:40 }}>No transactions yet</p>}
              {transactions.map(tx => (
                <div key={tx.transactionId}
                  style={{ display:"flex", justifyContent:"space-between", alignItems:"center",
                    padding:"10px 12px", borderRadius:8, marginBottom:6,
                    background:"#f8fafc", border:"1px solid #e2e8f0" }}>
                  <div>
                    <p style={{ margin:0, fontWeight:500, fontSize:14 }}>{tx.note}</p>
                    <p style={{ margin:0, color:"#888", fontSize:12 }}>
                      {new Date(tx.transactionDate).toLocaleDateString("en-IN", { day:"2-digit", month:"short", year:"numeric" })}
                    </p>
                  </div>
                  <div style={{ display:"flex", alignItems:"center", gap:10 }}>
                    <span style={{ color:"#ef4444", fontWeight:700, fontSize:15 }}>₹{tx.amount.toFixed(2)}</span>
                    <button onClick={() => removeTransaction(tx.transactionId)}
                      style={{ background:"#fee2e2", color:"#dc2626", border:"none", borderRadius:6, padding:"3px 8px", cursor:"pointer", fontSize:12 }}>✕</button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
