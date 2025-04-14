<script>
	import { onMount } from 'svelte';
  
	let message = "Chargement...";
  
	onMount(async () => {
	  const res = await fetch("http://localhost:8080/api/hello");
	  message = await res.text();
	});
  </script>
  
  <h1>{message}</h1>
  