import { useEffect, useState } from 'react';

export function useHashRoute(defaultRoute = '/login') {
  const [route, setRoute] = useState(() => window.location.hash.replace(/^#/, '') || defaultRoute);

  useEffect(() => {
    const update = () => setRoute(window.location.hash.replace(/^#/, '') || defaultRoute);
    window.addEventListener('hashchange', update);
    return () => window.removeEventListener('hashchange', update);
  }, [defaultRoute]);

  function navigate(nextRoute: string) {
    window.location.hash = `#${nextRoute}`;
  }

  return { route, navigate };
}
