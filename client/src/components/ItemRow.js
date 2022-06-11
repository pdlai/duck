export default function ItemRow(props) {

	const { name, stock, id, capacity } = props.item;

	return (
		<tr>
			<td>{id}</td>
			<td>{name}</td>
			<td>{stock}</td>
			<td>{capacity}</td>
			<td>{capacity-stock}</td>
		</tr>
	);
}