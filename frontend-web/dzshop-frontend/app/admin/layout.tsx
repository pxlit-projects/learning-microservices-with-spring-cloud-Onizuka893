import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";
import { ProductProvider } from "@/lib/productContext";
import { CategoryProvider } from "@/lib/categoryContext";

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <ProductProvider>
      <CategoryProvider>
        <SidebarProvider>
          <AppSidebar />
          <main className="flex-1 overflow-y-auto p-8">
            <SidebarTrigger />
            {children}
          </main>
        </SidebarProvider>
      </CategoryProvider>
    </ProductProvider>
  );
}
