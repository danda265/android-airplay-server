# Espelha Danda

App receptor de espelhamento de tela para TV (Android TV / TV box). O celular
espelha a tela na TV; o app roda **na TV** e recebe.

- **iPhone / iPad / Mac:** espelham nativo (AirPlay), sem instalar nada no celular.
- **Android:** por enquanto precisa de um app emissor (planejado na Fase 3).

Fork de [jqssun/android-airplay-server](https://github.com/jqssun/android-airplay-server)
(GPL-3.0). Este projeto herda a licença **GPL-3.0** — se distribuir, o código-fonte
deve permanecer aberto.

## Como compilar (na nuvem)

O app compila **FFmpeg + OpenSSL do fonte**, o que exige ambiente Unix — não
compila no Windows. A compilação roda no **GitHub Actions (Ubuntu)**:

1. Editar o código e dar push na branch de trabalho.
2. O workflow `.github/workflows/debug-apk.yml` compila um APK de debug (armeabi-v7a)
   e publica como artefato.
3. Baixar: `gh run download <run-id> -R danda265/android-airplay-server`
4. Instalar na TV: `adb uninstall io.github.jqssun.airplay` e `adb install app-debug.apk`
   (a assinatura de debug muda entre builds, por isso desinstalar antes).

## Personalizações (vs. upstream)

- Nome/marca: **Espelha Danda** (aparece na TV e na lista de espelhamento do celular).
- Tela de espera própria (`app/.../ui/EspelhaWaiting.kt`): marca, instruções, IP e QR code.
- Defaults inteligentes (`app/.../Prefs.kt`), calibrados numa TCL Android 9:
  - `H.265 (HEVC)` **desligado** — evita mosaico em decodificadores antigos.
  - `Auto resolution` **ligado** — evita tela preta.
  - `Iniciar no boot` e `rodar em segundo plano` **desligados** — "app educado":
    ao sair, libera a porta 7000 e o decodificador de vídeo (outros apps da TV
    seguem funcionando sem reiniciar).
  - `Máximo FPS` = 60.

## Estado

- **Fase 1** — espelhamento do iPhone funcionando. ✅
- **Fase 2** — app próprio "Espelha Danda": pipeline de build na nuvem, defaults e
  tela custom. ✅
- **Fase 3** (a fazer) — app emissor de Android (MediaProjection); aí o QR
  "escaneie e conecte" ganha função de verdade.

## Créditos

Protocolo AirPlay e base do app: projeto **UxPlay** e **jqssun/android-airplay-server**.
