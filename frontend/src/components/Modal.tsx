import type { ReactNode } from 'react';

type ModalProps = {
  title: string;
  open: boolean;
  children: ReactNode;
  onClose: () => void;
};

export function Modal({ title, open, children, onClose }: ModalProps) {
  if (!open) {
    return null;
  }

  return (
    <div className="modal-backdrop" role="presentation" onClick={onClose}>
      <section className="modal" role="dialog" aria-modal="true" aria-label={title} onClick={(event) => event.stopPropagation()}>
        <header className="panel-header">
          <h2>{title}</h2>
          <button className="secondary-button" type="button" onClick={onClose}>Close</button>
        </header>
        {children}
      </section>
    </div>
  );
}
