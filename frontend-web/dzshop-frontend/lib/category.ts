import { Product } from "./product";

export interface Category {
  id: number;
  name: string;
  products: Product[];
}

// private Long id;
// private String name;
// private List<ProductResponse> products = new ArrayList<>();
