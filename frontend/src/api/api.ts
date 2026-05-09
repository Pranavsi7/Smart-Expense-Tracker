import axios from 'axios';

/**
 * BASE URL resolution:
 *  - Local dev (npm run dev)  : vite.config.ts proxy handles /api → localhost:8080
 *  - Docker                   : nginx proxies /api → expense-backend:8080
 *  - Vercel (production)      : VITE_API_URL set in Vercel dashboard to Railway URL
 */
const BASE_URL = import.meta.env.VITE_API_URL
  ? `${import.meta.env.VITE_API_URL}/api`
  : '/api';

const api = axios.create({ baseURL: BASE_URL });

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwt_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// ── Auth ──────────────────────────────────────────────────────────────────
export const loginUser = (email: string, password: string) =>
  api.post<{ token: string }>('/users/login', { email, password });

export const registerUser = (
  firstName: string, lastName: string, email: string, password: string
) =>
  api.post<{ token: string }>('/users/register', {
    firstName, lastName, email, password,
  });

// ── Categories ────────────────────────────────────────────────────────────
export const getCategories = () =>
  api.get<Category[]>('/categories');

export const createCategory = (title: string, description: string) =>
  api.post<Category>('/categories', { title, description });

export const updateCategory = (id: number, title: string, description: string) =>
  api.put(`/categories/${id}`, { title, description });

export const deleteCategory = (id: number) =>
  api.delete(`/categories/${id}`);

// ── Transactions ──────────────────────────────────────────────────────────
export const getTransactions = (categoryId: number) =>
  api.get<Transaction[]>(`/categories/${categoryId}/transactions`);

export const createTransaction = (
  categoryId: number, amount: number, note: string, transactionDate: number
) =>
  api.post<Transaction>(`/categories/${categoryId}/transactions`, {
    amount, note, transactionDate,
  });

export const deleteTransaction = (categoryId: number, transactionId: number) =>
  api.delete(`/categories/${categoryId}/transactions/${transactionId}`);

// ── Local types ───────────────────────────────────────────────────────────
interface Category {
  categoryId: number; userId: number; title: string;
  description: string; totalExpense: number;
}
interface Transaction {
  transactionId: number; categoryId: number; userId: number;
  amount: number; note: string; transactionDate: number;
}
