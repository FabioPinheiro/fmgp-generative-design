import { resolve } from 'path'
import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";
// import { VitePWA } from 'vite-plugin-pwa'
// import viteCompression from 'vite-plugin-compression';

export default defineConfig(({ command, mode, ssrBuild }) => {
  // const serviceworkerSrc = '../serviceworker/target/scala-3.3.6/fmgp-serviceworker-' + ((command === 'serve') ? 'fastopt' : 'opt')
  //resolve(__dirname, 'serviceworker/target/scala-3.3.6/fmgp-serviceworker-fastopt'),

  return {
    root: './vite',
    build: {
      outDir: './dist',
      // minify: 'terser', // defualt is 'esbuild'
      manifest: true,
      // sourcemap: true,
    },
    preview: {
      port: 8090,
      // open: true,
    },
    server: {
      cors: true,
      proxy: {
        // '/ops': 'http://localhost:8080',
        // '/makeKey/X25519': 'http://localhost:8080',
        // '/makeKey/Ed25519': 'http://localhost:8080',
        // '^/ws': {
        //   target: 'ws://localhost:8080',
        //   ws: true,
        // },
        // '/sw.js': 'http://localhost:8080',
      },
    },
    plugins: [
      scalaJSPlugin({
        cwd: '.', // path to the directory containing the sbt build // default: '.'
        projectID: 'webapp', // sbt project ID from within the sbt build to get fast/fullLinkJS from
        uriPrefix: 'scalajs', // URI prefix of imports that this plugin catches
      }),
      // VitePWA({
      //   devOptions: {
      //     enabled: true,
      //     type: "module", //  SyntaxError: Cannot use import statement outside a module
      //   },
      //   srcDir: serviceworkerSrc,
      //   filename: "sw.js", //@default 'sw.js'
      //   strategies: "injectManifest",
      //   injectRegister: 'inline', //null, 'inline', // https://vite-pwa-org.netlify.app/guide/register-service-worker.html
      //   injectManifest: {
      //     injectionPoint: "self.__WB_MANIFEST", // null, // 'self__WB_MANIFEST', // undefined
      //     // additionalManifestEntries: ['robots.txt'],
      //     maximumFileSizeToCacheInBytes: 18000000,//12000000,
      //   },
      //   // includeAssets: ['favicon.ico'],
      //   manifest: {
      //     "name": "Sandbox DID Comm v2",
      //     "short_name": "ScalaDID",
      //     "description": "Demo Sandbox DID Comm v2",
      //     "theme_color": "#6200ee",
      //     "background_color": "#018786",
      //     "display": "standalone",
      //     "scope": "/",
      //     "start_url": "/",
      //     "lang": "en",
      //     "categories": ["education", "did"],
      //     "icons": [
      //       { "src": "https://web.dev/_pwa/web/icons/icon-72x72.png", "sizes": "72x72", "type": "image/png" },
      //       { "src": "https://web.dev/_pwa/web/icons/icon-144x144.png", "sizes": "144x144", "type": "image/png", "purpose": "any maskable" },
      //       { "src": "https://web.dev/_pwa/web/icons/icon-512x512.png", "sizes": "512x512", "type": "image/png" },
      //     ],
      //     "protocol_handlers": [{ "protocol": "did", "url": "/#/resolver/%s" }],
      //     "shortcuts": [{ "name": "Mediator", "url": "/#/mediator" }],
      //     "splash_pages": null,
      //     "share_target": {
      //       "action": "/#/?_oob=",
      //       "method": "POST",
      //       "enctype": "multipart/form-data",
      //       "params": {
      //         "title": "name",
      //         "text": "description",
      //         "url": "link",
      //         "files": [
      //           {
      //             "name": "lists",
      //             "accept": ["text/csv", ".csv"]
      //           },
      //           {
      //             "name": "photos",
      //             "accept": ["image/svg+xml", ".svg"]
      //           },
      //           {
      //             "name": "image",
      //             "accept": ["image/jpeg", "image/jfif", ".jpeg", ".jpe", ".jpg", ".jfif", ".jfi"]
      //           },
      //           {
      //             "name": "file",
      //             "accept": ["*/*"]
      //           }
      //         ]
      //       }
      //     },
      //   }
      // }),
      // viteCompression(),
    ]
  }
});
