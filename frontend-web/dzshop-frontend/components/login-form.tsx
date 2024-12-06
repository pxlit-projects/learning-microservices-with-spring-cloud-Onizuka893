"use client";

import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Label } from "./ui/label";
import { useUserContext } from "@/lib/userContext";
import { useRouter } from "next/navigation";
import { useState } from "react";

export default function LoginForm() {
  const [usName, setUsName] = useState("");
  const [pass, setPass] = useState("");
  const [error, setError] = useState("");
  const { setUsername, setPassword, setUserId } = useUserContext();
  const router = useRouter();

  const handleLogin = () => {
    if (usName && pass) {
      setError("");
      setUsername(usName);
      setPassword(pass);
      if (usName === "admin") {
        setUserId(1111);
        router.push("/admin");
      } else {
        setUserId(1234);
        router.push("/shop");
      }
    } else {
      setError("username or password wrong");
    }
  };

  return (
    <div className="flex-row w-[300px] border rounded-lg shadow-md p-6">
      <div className="m-2">
        <Label>Username</Label>
        <Input
          type="text"
          value={usName}
          onChange={(e) => setUsName(e.target.value)}
          placeholder="username"
        />
      </div>
      <div className="m-2">
        <Label>Password</Label>
        <Input
          type="password"
          value={pass}
          onChange={(e) => setPass(e.target.value)}
          placeholder="password"
        />
      </div>
      <div className="my-4 mx-2">
        <Button onClick={handleLogin} className="w-full">
          Login
        </Button>
      </div>
      {error && <div className="my-4 mx-2 text-red-500">{error}</div>}
    </div>
  );
}
