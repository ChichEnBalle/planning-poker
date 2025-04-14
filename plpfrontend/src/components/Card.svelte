<script lang="ts">
    export let value: string;
    export let selected: boolean;
    export let onSelect: (value: string) => void;
  
    function handleClick() {
      onSelect(value);
    }
  </script>
  
  <style>
    .card {
      border: 1px solid black;
      border-radius: 8px;
      padding: 18px;
      margin: 2px;
      text-align: center;
      font-weight: bold;
      cursor: pointer;
      background-color: white;
      transition: 0.3s
    }
  
    .card.selected {
      background-color: rgb(147, 234, 245);
      border-color: #00bfff;
    }
  
    .card:hover {
      background-color: #9bd5fa;
    }
  </style>
  
  <div class="card {selected ? 'selected' : ''}" on:click={handleClick}>
    <p>{value}</p>
  </div>