import { AnimatePresence, motion } from "framer-motion";
import { useEffect, useState } from "react";
import { getToken } from "@/utils/token";
import scss from "./AuthGuard.module.scss";
import { useRouter } from "next/router";
import fetcher from "@/utils/fetcher";
// import { useUserStore } from "@/Store/UsersStore";
import Header from "../layouts/Header";
import HomeHeader from "../layouts/HomeHeader";
import Footer from "../layouts/Footer";
import HomeFooter from "../layouts/HomeFooter";
import useUserStore from "@/Store/UsersStore";

const NO_HEADER_ROUTES = ["/login", "/register"];
const PUBLIC_ROUTES = new Set(["/login", "/register"]);
const DEFAULT_PUBLIC_ROUTE = "/login";

const isProtectedRoute = (route: string) => !PUBLIC_ROUTES.has(route);

function AuthGuard({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const currentRoute = router.pathname;
  // const setUser = useUserStore.use.setUser();
  const setUser = useUserStore((state:any) => state.setUser);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const token = getToken();
    if (currentRoute === "/") {
      if (token) {
        router.push("/dashboard");
      } else {
        router.push("/login");
      }
    }
    if (!token) {
      setStatus(false);
      return;
    }
    const controller = new AbortController();
    const { signal } = controller;
    fetcher("/verify", "POST", null, signal).then((json) => {
      if (signal.aborted) {
        return;
      }
      if (!json || json.error) {
        setStatus(false);
        return;
      }
      setStatus(json.verified);
      if (json.verified) {
        const { user } = json;
        setUser(user);
      }
    });
    return () => controller.abort();
  }, [currentRoute]);

  const setStatus = (isValid: boolean) => {
    setTimeout(() => {
      if (!isValid && isProtectedRoute(currentRoute)) {
        router.replace(DEFAULT_PUBLIC_ROUTE);
      }
      if (isValid && !isProtectedRoute(currentRoute)) {
        router.push("/dashboard");
      }
      setIsAuthenticated(isValid);
      setIsLoading(false);
    }, 500);
  };

  const handleRegisterClick = () => {
    router.push("/register");
  };

  return (
    !isLoading &&
    (isAuthenticated || (!isAuthenticated && PUBLIC_ROUTES.has(currentRoute))) && (
      <div 
      // className="flex flex-column w-100 h-100"
      >
        <div
          // style={{
          //   marginTop: isProtectedRoute(currentRoute) ? "4rem" : "0rem",
          //   marginBottom: isProtectedRoute(currentRoute) ? "2rem" : "0rem",
          //   display: "flex",
          //   flexDirection: "column",
          //   height: isProtectedRoute(currentRoute) ? "" : "100vh",
          // }}
        >
          {!NO_HEADER_ROUTES.includes(currentRoute) ? (
            <Header onRegisterClick={handleRegisterClick} />
          ) : (
            <HomeHeader />
          )}
          {children}
          {NO_HEADER_ROUTES.includes(currentRoute) ? (
            <Footer />
          ) : (
            <HomeFooter />
          )}
        </div>
      </div>
    )
  );
}

export default AuthGuard;
