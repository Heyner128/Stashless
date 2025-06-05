export class InputTesting {
  static insertText(
    input: HTMLInputElement | HTMLTextAreaElement,
    text: string,
  ) {
    input.value = text;
    input.dispatchEvent(new Event('input'));
  }
}