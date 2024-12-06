export interface Cart {
  id: number;
  userId: number;
  total: number;
  items: CartItem[];
}

export interface CartItem {
  productId: number;
  productName: string;
  quantity: number;
  price: number;
}
