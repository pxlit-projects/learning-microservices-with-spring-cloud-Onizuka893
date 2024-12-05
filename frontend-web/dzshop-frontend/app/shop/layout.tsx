import { ProductProvider } from "@/lib/productContext";
import { CategoryProvider } from "@/lib/categoryContext";

export default function ProductsLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <ProductProvider>
      <CategoryProvider>
        <main className="flex-1 overflow-y-auto">{children}</main>
      </CategoryProvider>
    </ProductProvider>
  );
}
