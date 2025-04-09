
import { NgStyle } from '@angular/common';
import { Component, effect, ElementRef, HostListener, Injector, input,  InputSignal, model, ModelSignal, OnInit, signal, ViewChild, WritableSignal } from '@angular/core';
import { autoUpdate, computePosition, offset, shift, arrow } from '@floating-ui/dom';

@Component({
  selector: 'app-popover',
  imports: [NgStyle],
  templateUrl: './popover.component.html',
  styleUrl: './popover.component.scss'
})
export class PopoverComponent implements OnInit {
  positionStyles: Record<string, string> = {};
  arrowPositionStyles: Record<string, string> = {};
  isOpen: ModelSignal<boolean> = model.required();
  anchorElement: InputSignal<HTMLElement>  = input.required<HTMLElement>();

  x: WritableSignal<number> = signal(0);
  y: WritableSignal<number> = signal(0);

  @ViewChild('popover')
  popover!: ElementRef<HTMLDivElement>;

  @ViewChild('arrow')
  arrow!: ElementRef<HTMLDivElement>;

  constructor(private readonly injector: Injector, private readonly elementRef: ElementRef) {}
  
  ngOnInit(): void {
    this.initAutoUpdate();
  }

  initAutoUpdate() {
    let cleanAutoUpdate: () => void | undefined;
    effect((onCleanup) => {
      if (this.isOpen()) {
        cleanAutoUpdate = autoUpdate(
          this.anchorElement(),
          this.popover.nativeElement,
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
    computePosition(
      this.anchorElement(),
      this.popover.nativeElement,
      {
        placement: 'bottom',
        middleware: [
          offset(10),
          shift({
            padding: 10
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
