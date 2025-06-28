
import { NgStyle } from '@angular/common';
import { ChangeDetectionStrategy, ChangeDetectorRef, Component, effect, ElementRef, HostListener, Injector, input,  InputSignal, model, ModelSignal, OnInit, signal, ViewChild, WritableSignal } from '@angular/core';
import { autoUpdate, computePosition, offset, shift, arrow } from '@floating-ui/dom';

@Component({
  selector: 'app-popover',
  imports: [NgStyle],
  host: {
    '[style]': 'positionStyles',
    '[hidden]': '!isOpen()',
    'tabindex': '-1',
  },
  templateUrl: './popover.component.html',
  styleUrl: './popover.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PopoverComponent implements OnInit {
  positionStyles: Record<string, string> = {};
  arrowPositionStyles: Record<string, string> = {};
  isOpen: ModelSignal<boolean> = model.required();
  offset: InputSignal<number> = input<number>(10);
  shift: InputSignal<number> = input<number>(10);
  anchorElement: InputSignal<HTMLElement>  = input.required<HTMLElement>();
  x: WritableSignal<number> = signal(0);
  y: WritableSignal<number> = signal(0);
  hasArrow: InputSignal<boolean> = input<boolean>(false);

  @ViewChild('arrow')
  arrow!: ElementRef<HTMLDivElement>;

  constructor(
    private readonly injector: Injector,
    private readonly elementRef: ElementRef,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {}
  
  ngOnInit(): void {
    this.initAutoUpdate();
  }

  initAutoUpdate() {
    let cleanAutoUpdate: () => void | undefined;
    effect((onCleanup) => {
      if (this.isOpen()) {
        cleanAutoUpdate = autoUpdate(
          this.anchorElement(),
          this.elementRef.nativeElement,
          this.updatePosition.bind(this)
        );
      }

      onCleanup(() => {
        if (cleanAutoUpdate && !this.isOpen()) {
          cleanAutoUpdate();
        }
      });
    }, {injector: this.injector});
  }

  @HostListener('document:click', ['$event'])
  closeOnBlur(event: MouseEvent) {
    if(!this.elementRef.nativeElement.contains(event.target)) {
      this.isOpen.set(false);
    }
  }
  
  updatePosition() {
    this.changeDetectorRef.markForCheck();
    computePosition(
      this.anchorElement(),
      this.elementRef.nativeElement,
      {
        placement: 'bottom',
        middleware: [
          offset(this.offset()),
          shift({
            padding: this.shift(),
          }),
          arrow({
            element: this.arrow.nativeElement,
          })
        ],
      }
    ).then(
      ({ x, y, placement, middlewareData}) => {
        const {x: arrowX, y: arrowY} = middlewareData.arrow ?? {};

        this.x.set(x);
        this.y.set(y);
        this.positionStyles = {
          left: `${this.x()}px`,
          top: `${this.y()}px`,
        };

        const staticSide = {
          top: 'bottom',
          right: 'left',
          bottom: 'top',
          left: 'right',
        }[placement.split('-')[0]];
 

        this.arrowPositionStyles = {
          display: this.hasArrow() ? 'block' : 'none',
          left: arrowX != null ? `${arrowX}px` : '',
          top: arrowY != null ? `${arrowY}px` : '',
          right: '',
          bottom: '',
          [staticSide ?? '']: '-4px',
        };
      }
    );
  }
}
