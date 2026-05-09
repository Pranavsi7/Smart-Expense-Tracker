export interface Category {
  categoryId: number;
  userId: number;
  title: string;
  description: string;
  totalExpense: number;
}

export interface Transaction {
  transactionId: number;
  categoryId: number;
  userId: number;
  amount: number;
  note: string;
  transactionDate: number;
}
