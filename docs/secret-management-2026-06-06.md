# Secret Management Update

## Summary

`app/google-services.json` is local Firebase configuration and must not be tracked in Git. The project now keeps a placeholder template at `app/google-services.json.example` and ignores the real `app/google-services.json` file.

## Current Policy

- Keep the real `app/google-services.json` only on local developer machines or in a private CI secret store.
- Copy `app/google-services.json.example` to `app/google-services.json` before building a fresh checkout.
- Restrict or rotate Firebase/Google API keys in the Firebase or Google Cloud console after any public exposure.
- Do not commit generated Firebase client config files that contain project-specific keys.

## Related Pages

- [Firebase 설정 적용 기록](firebase-settings-applied-2026-06-06.md)
- [Firebase MVP Rules Update](firebase-mvp-rules-2026-06-04.md)
- [Firebase Storage Rules Update](firebase-storage-rules-2026-06-05.md)
