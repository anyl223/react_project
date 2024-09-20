import "@/styles/globals.css";
import type { AppProps } from "next/app";
import { ChakraProvider, extendTheme } from "@chakra-ui/react";
import { Inter } from "next/font/google";
import { ToastContainer } from "react-toastify";
import AuthGuard from "@/components/AuthGuard";
import nProgress from "nprogress";
import { useRouter } from "next/router";
import { useEffect } from "react";


const inter = Inter({ subsets: ["latin"] });
const chakraTheme = extendTheme({
  fonts: {
    heading: `${inter.style.fontFamily}, sans-serif`,
    body: `${inter.style.fontFamily}, sans-serif`,
  },
});

export default function App({ Component, pageProps }: AppProps) {
  const router = useRouter();

  useEffect(() => {
    router.events.on("routeChangeStart", () => nProgress.start());
    router.events.on("routeChangeComplete", () => nProgress.done());
    router.events.on("routeChangeError", () => nProgress.done());

    const handleRightClick = (evt: MouseEvent) => {
      evt.preventDefault();
    };
    document.addEventListener("contextmenu", handleRightClick);

    return () => {
      router.events.off("routeChangeStart", () => nProgress.start());
      router.events.off("routeChangeComplete", () => nProgress.done());
      router.events.off("routeChangeError", () => nProgress.done());

      document.removeEventListener("contextmenu", handleRightClick);
    };
  }, []);

  return <>
  <ChakraProvider theme={chakraTheme}>
  <ToastContainer
        position="bottom-center"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        // theme="colored"
      />
        <AuthGuard>
        <Component {...pageProps} />
        </AuthGuard>
      </ChakraProvider></>
}
