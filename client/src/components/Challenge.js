import React, { useState } from "react";
import ItemRow from "../components/ItemRow";

export default function Challenge() {

  const [items, setItems] = useState([]);
  const [cost, setCost] = useState(0);

  function getLowStock(){
    fetch("http://localhost:4567/low-stock")
      .then(response => response.json())
      .then(data => setItems(data['items']))
      .then(console.log(items))
  }

  function getRestockCost(){
    let data = {};
    items.forEach( item => {
      const name = item.name;
      const restock = item.capacity - item.stock;
      data[name] = restock;
    })
    const requestOptions = {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    };
    fetch("http://localhost:4567/restock-cost", requestOptions)
      .then(response => response.json())
      .then(data => console.log(data))
  }

  return (
    <>
      <table>
        <thead>
          <tr>
            <td>SKU</td>
            <td>Item Name</td>
            <td>Amount in Stock</td>
            <td>Capacity</td>
            <td>Order Amount</td>
          </tr>
        </thead>
        <tbody>
          {
            items.map( item => <ItemRow key={item.id} item={item} />)
          }
        </tbody>
      </table>
      {/* TODO: Display total cost returned from the server */}
      <div>Total Cost: ${cost}</div>
      <button onClick={getLowStock}>Get Low-Stock Items</button>
      <button onClick={getRestockCost}>Determine Re-Order Cost</button>
    </>
  );
}
