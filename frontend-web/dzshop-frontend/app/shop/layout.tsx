import { Navbar } from "@/components/navbar";

export default function ProductsLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <main className="flex-1 overflow-y-auto min-h-screen">
      <Navbar />
      {children}
    </main>
  );
}
