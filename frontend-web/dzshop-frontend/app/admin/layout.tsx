import { SidebarProvider, SidebarTrigger } from "@/components/ui/sidebar";
import { AppSidebar } from "@/components/app-sidebar";
import { LogbookProvider } from "@/lib/logbookContext";

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <LogbookProvider>
      <SidebarProvider>
        <AppSidebar />
        <main className="flex-1 overflow-y-auto p-8">
          <SidebarTrigger />
          {children}
        </main>
      </SidebarProvider>
    </LogbookProvider>
  );
}
