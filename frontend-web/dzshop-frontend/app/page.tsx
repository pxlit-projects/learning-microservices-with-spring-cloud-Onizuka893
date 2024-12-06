import { Button } from "@/components/ui/button";
import Link from "next/link";

export default function Home() {
  return (
    <div className="flex justify-center items-center min-h-dvh">
      <Button asChild>
        <Link href={"/shop"}>Enter Shop</Link>
      </Button>
    </div>
  );
}
