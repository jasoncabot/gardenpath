interface QueueItem {
    priority: number,
    value: any
}

class Queue {
    internal: QueueItem[] = [];

    constructor() {
        this.internal = [];
    }

    isEmpty = () => {
        return this.internal.length === 0;
    }

    bubbleUp = (index: number) => {
        let currentIndex = index;

        while (currentIndex > 0) {
            const parentIndex = this.parentIndex(currentIndex);

            if (this.compare(currentIndex, parentIndex) <= 0) {
                break;
            }

            this.swapItems(currentIndex, parentIndex);
            currentIndex = parentIndex;
        }
    }

    compare = (i: number, j: number) => {
        const x = this.internal[i];
        const y = this.internal[j];
        return y.priority - x.priority;
    }

    childIndices = (i: number) => {
        const indices = [];
        const left = this.leftChildIndex(i);
        const right = this.rightChildIndex(i);

        if (left < this.internal.length) {
            indices.push(left);

            if (right < this.internal.length) {
                indices.push(right);
            }
        }

        return indices;
    }

    leftChildIndex = (i: number) => {
        return (2 * i) + 1;
    }

    maxPriorityChildIndex = (i: number) => {
        const [left, right] = this.childIndices(i);

        if (left) {
            if (right && this.compare(right, left) > 0) {
                return right;
            }

            return left;
        }

        return -1;
    }

    parentIndex = (i: number) => {
        return Math.floor((i - 1) / 2);
    }

    rightChildIndex = (i: number) => {
        return (2 * i) + 2;
    }

    isPriorityOrdered: (index: number) => boolean = (index: number) => {
        const indices = this.childIndices(index);

        for (const i in indices) {
            if (this.compare(index, indices[i]) < 0) {
                return false;
            }
        }

        return true;
    }

    shiftDown = (index: number) => {
        let currentIndex = index;

        while (!this.isPriorityOrdered(currentIndex)) {
            const maxPriorityChildIndex = this.maxPriorityChildIndex(currentIndex);
            this.swapItems(currentIndex, maxPriorityChildIndex);
            currentIndex = maxPriorityChildIndex;
        }
    }

    swapItems = (i: number, j: number) => {
        [this.internal[i], this.internal[j]] = [this.internal[j], this.internal[i]];
    }

    dequeue() {
        if (this.isEmpty()) throw new Error("Unable to dequeue from empty queue");

        const front = this.peek();
        const last: QueueItem = this.internal.pop()!;

        this.internal[0] = last;
        this.shiftDown(0);

        return front;
    }

    enqueue = (priority: number, value: any) => {
        const item: QueueItem = { priority, value };
        this.internal.push(item);
        this.bubbleUp(this.internal.length - 1);
        return this;
    }

    peek = () => {
        return this.internal[0];
    }
}

export { Queue, QueueItem }