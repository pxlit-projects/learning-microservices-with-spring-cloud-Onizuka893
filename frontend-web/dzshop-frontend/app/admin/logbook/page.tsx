"use client";
import { Label } from "@/components/ui/label";
import { useLogbookContext } from "@/lib/logbookContext";

export default function LogbookPage() {
  const { entries } = useLogbookContext();

  if (!entries) return <div>Loading</div>;

  return (
    <div className="flex-row text-3xl justify-between">
      <div className="p-2 m-2">Logbook Entries</div>
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {entries.map((e) => (
          <div key={e.id} className="border shadow-sm rounded-md p-2 m-2">
            <Label>Message:</Label>
            <div className="text-sm">{e.message}</div>
            <Label>Producer:</Label>
            <div className="text-sm">{e.producer}</div>
            <Label>Date:</Label>
            <div className="text-sm">{e.created}</div>
          </div>
        ))}
      </div>
    </div>
  );
}
