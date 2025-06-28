import { animate, state, style, transition, trigger, AnimationEvent } from '@angular/animations';
import { ChangeDetectionStrategy, Component, ElementRef, HostListener, linkedSignal, signal, ViewChild } from '@angular/core';
import { PopoverComponent } from "../../popover/popover.component";

@Component({
  selector: 'app-search',
  imports: [PopoverComponent],
  animations: [trigger(
    'openClose', [
      state(
        'open',
        style({
          minWidth: 'calc(0.6 * var(--fixed-size))',
          maxWidth: 'var(--fixed-size)'
        })
      ),
      state(
        'closed',
        style({
          position: 'absolute',
          width: '1px',
          height: '1px',
          margin: '-1px',
          padding: 0,
          overflow: 'hidden',
          clip: 'rect(0, 0, 0, 0)',
          border: 0,
          whiteSpace: 'nowrap'
        })
      ),
      transition('closed => open', [animate('0.2s')]),
    ]
  )],
  templateUrl: './search.component.html',
  styleUrl: './search.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchComponent {
  isSearchOpen = signal(false);
  isInputFocused = signal(false);
  isSearchCompletelyOpen = linkedSignal(() => this.isSearchOpen() && this.isInputFocused());
  @ViewChild('input') searchInput: ElementRef<HTMLInputElement> | undefined;

  constructor(private readonly elementRef: ElementRef) {}

  onAnimationChange(event: AnimationEvent) {
    if (
      this.searchInput &&
      event.toState === 'open' &&
      event.phaseName === 'done'
    ) {
      this.searchInput.nativeElement.focus();
      this.isInputFocused.set(true);
    }
  }

  open() {
    this.isSearchOpen.set(true);
  }

  @HostListener('document:click', ['$event'])
  close(event: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isSearchOpen.set(false);
      this.isInputFocused.set(false);
    }
  }
}
